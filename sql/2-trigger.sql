-- Triggers de gotta_taste. État courant du schéma : pour une base existante,
-- appliquer les scripts de sql/migrations/ (voir sql/migrations/README.md).
--
-- Règles portées par la base :
--   * recipe.cook_time        = somme des cook_time des étapes
--   * recipe_stock            : une ligne par recette, créée avec la recette ; décrémentée
--                               par les ventes (création / modification / suppression)
--   * recipe_sell.reste       = argent - combien * prix de la recette
--   * commission              : calculée à chaque vente selon la règle en vigueur à la date de vente
--   * recipe_price_history    : une ligne par changement de prix
--   * ingredient_stock        : une ligne par ingrédient, créée avec lui
--   * production              : consomme les ingrédients de la recette et alimente recipe_stock
-- Les refus (stock insuffisant...) sont des RAISE EXCEPTION dont le message est
-- affiché tel quel à l'utilisateur (dao.BusinessRuleException).


-- ---------------------------------------------------------------------------
-- Temps de préparation d'une recette = somme des étapes
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION recompute_recipe_cook_time(p_id_recipe INT)
RETURNS VOID AS $$
BEGIN
    UPDATE recipe
    SET cook_time = (
        SELECT COALESCE(SUM(cook_time::interval), '00:00:00')::time
        FROM step
        WHERE step.id_recipe = p_id_recipe
    )
    WHERE recipe.id_recipe = p_id_recipe;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_recipe_cook_time()
RETURNS TRIGGER AS $$
BEGIN
    -- Sur DELETE, NEW est NULL : on recalcule à partir de OLD (et des deux recettes
    -- si une étape change de recette).
    IF TG_OP <> 'DELETE' THEN
        PERFORM recompute_recipe_cook_time(NEW.id_recipe);
    END IF;
    IF TG_OP = 'DELETE' OR (TG_OP = 'UPDATE' AND OLD.id_recipe <> NEW.id_recipe) THEN
        PERFORM recompute_recipe_cook_time(OLD.id_recipe);
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trigger_update_cook_time
AFTER INSERT OR UPDATE OR DELETE
ON step
FOR EACH ROW
EXECUTE FUNCTION update_recipe_cook_time();


-- ---------------------------------------------------------------------------
-- Stock : une ligne par recette, créée à 0 avec la recette
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION init_recipe_stock()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO recipe_stock (id_recipe, reste)
    VALUES (NEW.id_recipe, 0)
    ON CONFLICT (id_recipe) DO NOTHING;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_init_recipe_stock
AFTER INSERT ON recipe
FOR EACH ROW
EXECUTE FUNCTION init_recipe_stock();


-- ---------------------------------------------------------------------------
-- Vente : reste rendu au client
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION calculate_reste()
RETURNS TRIGGER AS $$
BEGIN
    -- reste = argent - combien * recipe.price (CHECK reste >= 0 : argent insuffisant)
    NEW.reste := NEW.argent - NEW.combien * (
        SELECT price
        FROM recipe
        WHERE id_recipe = NEW.id_recipe
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trigger_calculate_reste
BEFORE INSERT OR UPDATE ON recipe_sell
FOR EACH ROW
EXECUTE FUNCTION calculate_reste();


-- ---------------------------------------------------------------------------
-- Vente : consommation du stock (création, modification, suppression)
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION update_recipe_stock()
RETURNS TRIGGER AS $$
DECLARE
    v_remaining INT;
BEGIN
    -- Restitution de l'ancienne vente (modification ou suppression)
    IF TG_OP IN ('UPDATE', 'DELETE') THEN
        UPDATE recipe_stock
        SET reste = reste + OLD.combien
        WHERE id_recipe = OLD.id_recipe;
    END IF;

    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    END IF;

    IF NEW.combien <= 0 THEN
        RAISE EXCEPTION 'La quantité vendue doit être supérieure à zéro';
    END IF;

    -- Décrément atomique : la condition reste >= combien est réévaluée sous verrou
    -- de ligne, deux ventes simultanées ne peuvent pas rendre le stock négatif.
    UPDATE recipe_stock
    SET reste = reste - NEW.combien
    WHERE id_recipe = NEW.id_recipe AND reste >= NEW.combien
    RETURNING reste INTO v_remaining;

    IF NOT FOUND THEN
        SELECT reste INTO v_remaining FROM recipe_stock WHERE id_recipe = NEW.id_recipe;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'Aucun stock défini pour cette recette : approvisionnez-la avant de la vendre';
        END IF;
        RAISE EXCEPTION 'Stock insuffisant : il reste % unité(s) de cette recette', v_remaining;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_update_recipe_stock
BEFORE INSERT OR UPDATE OR DELETE ON recipe_sell
FOR EACH ROW
EXECUTE FUNCTION update_recipe_stock();


-- ---------------------------------------------------------------------------
-- Commission du vendeur selon la règle en vigueur à la date de vente
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION calculate_commission()
RETURNS TRIGGER AS $$
DECLARE
    v_rule   commission_change%ROWTYPE;
    v_total  DECIMAL(12,2);
BEGIN
    -- Une vente modifiée est recalculée ; une vente supprimée emporte sa commission
    -- (FK commission.id_recipe_sell ON DELETE CASCADE).
    IF TG_OP = 'UPDATE' THEN
        DELETE FROM commission WHERE id_recipe_sell = OLD.id_recipe_sell;
    END IF;

    -- Règle la plus récente dont la date est antérieure ou égale à la date de vente
    SELECT * INTO v_rule
    FROM commission_change
    WHERE commission_change_date <= NEW.sell_date
    ORDER BY commission_change_date DESC, id_commission_change DESC
    LIMIT 1;

    IF NOT FOUND THEN
        RAISE NOTICE 'Aucune règle de commission applicable au % : vente % sans commission',
            NEW.sell_date, NEW.id_recipe_sell;
        RETURN NULL;
    END IF;

    v_total := NEW.combien * (SELECT price FROM recipe WHERE id_recipe = NEW.id_recipe);

    IF v_total >= v_rule.commission_change_value THEN
        INSERT INTO commission (id_recipe_sell, id_vendeur, id_recipe, commission_amount, commission_date)
        VALUES (NEW.id_recipe_sell, NEW.id_vendeur, NEW.id_recipe, v_total * v_rule.percent, NEW.sell_date);
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trigger_calculate_commission
AFTER INSERT OR UPDATE ON recipe_sell
FOR EACH ROW
EXECUTE FUNCTION calculate_commission();


-- ---------------------------------------------------------------------------
-- Historique des prix : une ligne par changement de prix, datée du jour du changement
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION log_price_change()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO recipe_price_history (id_recipe, price_before, price_after, change_date)
    VALUES (NEW.id_recipe, OLD.price, NEW.price, CURRENT_DATE);
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_log_price_change
AFTER UPDATE OF price ON recipe
FOR EACH ROW
WHEN (OLD.price IS DISTINCT FROM NEW.price)
EXECUTE FUNCTION log_price_change();


-- ---------------------------------------------------------------------------
-- Stock de matières premières : une ligne par ingrédient, créée à 0 avec lui
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION init_ingredient_stock()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO ingredient_stock (id_ingredient, reste)
    VALUES (NEW.id_ingredient, 0)
    ON CONFLICT (id_ingredient) DO NOTHING;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_init_ingredient_stock
AFTER INSERT ON ingredient
FOR EACH ROW
EXECUTE FUNCTION init_ingredient_stock();


-- ---------------------------------------------------------------------------
-- Production : consomme les ingrédients de la recette, alimente le produit fini
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION apply_production()
RETURNS TRIGGER AS $$
DECLARE
    v_missing RECORD;
BEGIN
    IF NOT EXISTS (SELECT 1 FROM recipe_ingredient WHERE id_recipe = NEW.id_recipe) THEN
        RAISE EXCEPTION 'Cette recette n''a aucun ingrédient : composez-la avant de lancer une production';
    END IF;

    -- Verrou sur les lignes de stock concernées : la vérification qui suit et le
    -- décrément forment un tout, deux productions simultanées ne peuvent pas
    -- consommer deux fois la même farine.
    PERFORM 1
    FROM ingredient_stock s
    JOIN recipe_ingredient ri ON ri.id_ingredient = s.id_ingredient
    WHERE ri.id_recipe = NEW.id_recipe
    FOR UPDATE OF s;

    SELECT i.ingredient_name,
           i.unit,
           ri.quantity * NEW.quantity AS besoin,
           COALESCE(s.reste, 0) AS dispo
    INTO v_missing
    FROM recipe_ingredient ri
    JOIN ingredient i ON i.id_ingredient = ri.id_ingredient
    LEFT JOIN ingredient_stock s ON s.id_ingredient = ri.id_ingredient
    WHERE ri.id_recipe = NEW.id_recipe
      AND COALESCE(s.reste, 0) < ri.quantity * NEW.quantity
    ORDER BY i.ingredient_name
    LIMIT 1;

    IF FOUND THEN
        RAISE EXCEPTION 'Matière première insuffisante : % (% % nécessaires, % disponibles)',
            v_missing.ingredient_name, v_missing.besoin, v_missing.unit, v_missing.dispo;
    END IF;

    UPDATE ingredient_stock s
    SET reste = s.reste - ri.quantity * NEW.quantity
    FROM recipe_ingredient ri
    WHERE ri.id_recipe = NEW.id_recipe
      AND s.id_ingredient = ri.id_ingredient;

    UPDATE recipe_stock
    SET reste = reste + NEW.quantity
    WHERE id_recipe = NEW.id_recipe;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_apply_production
BEFORE INSERT ON production
FOR EACH ROW
EXECUTE FUNCTION apply_production();
