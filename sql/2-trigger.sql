CREATE OR REPLACE FUNCTION update_recipe_cook_time()
RETURNS TRIGGER AS $$
BEGIN
    -- Mettre à jour le temps de cuisson dans la table recipe
    UPDATE recipe
    SET cook_time = (
        SELECT COALESCE(SUM(cook_time::interval), '00:00:00')::time
        FROM step
        WHERE step.id_recipe = NEW.id_recipe
    )
    WHERE recipe.id_recipe = NEW.id_recipe;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER trigger_update_cook_time
AFTER INSERT OR UPDATE OR DELETE
ON step
FOR EACH ROW
EXECUTE FUNCTION update_recipe_cook_time();


-- Créer une fonction pour calculer le reste
CREATE OR REPLACE FUNCTION calculate_reste()
RETURNS TRIGGER AS $$
BEGIN
    -- Calculer le reste : reste = argent - combien * recipe.price
    NEW.reste := NEW.argent - NEW.combien * (
        SELECT price 
        FROM recipe 
        WHERE id_recipe = NEW.id_recipe
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Créer un trigger sur la table recipe_sell
CREATE OR REPLACE TRIGGER trigger_calculate_reste
BEFORE INSERT OR UPDATE ON recipe_sell
FOR EACH ROW
EXECUTE FUNCTION calculate_reste();


-- Création de la fonction associée au trigger
CREATE OR REPLACE FUNCTION update_recipe_stock()
RETURNS TRIGGER AS $$
BEGIN
    -- Vérifie si la quantité restante est suffisante
    IF (SELECT reste FROM recipe_stock WHERE id_recipe = NEW.id_recipe) < NEW.combien THEN
        RAISE EXCEPTION 'Quantité insuffisante dans le stock pour id_recipe %', NEW.id_recipe;
    END IF;

    -- Mise à jour de la colonne `reste` dans `recipe_stock`
    UPDATE recipe_stock
    SET reste = reste - NEW.combien
    WHERE id_recipe = NEW.id_recipe;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Création du trigger
CREATE TRIGGER trg_update_recipe_stock
BEFORE INSERT ON recipe_sell
FOR EACH ROW
EXECUTE FUNCTION update_recipe_stock();




-- Fonction déclencheur : commission du vendeur selon la règle en vigueur à la date de vente
CREATE OR REPLACE FUNCTION calculate_commission()
RETURNS TRIGGER AS $$
DECLARE
    v_rule   commission_change%ROWTYPE;
    v_total  DECIMAL(12,2);
BEGIN
    -- Règle la plus récente dont la date est antérieure ou égale à la date de vente
    SELECT * INTO v_rule
    FROM commission_change
    WHERE commission_change_date <= NEW.sell_date
    ORDER BY commission_change_date DESC, id_commission_change DESC
    LIMIT 1;

    IF NOT FOUND THEN
        RAISE NOTICE 'Aucune règle de commission applicable au % : vente % sans commission',
            NEW.sell_date, NEW.id_recipe_sell;
        RETURN NEW;
    END IF;

    v_total := NEW.combien * (SELECT price FROM recipe WHERE id_recipe = NEW.id_recipe);

    IF v_total >= v_rule.commission_change_value THEN
        INSERT INTO commission (id_vendeur, id_recipe, commission_amount, commission_date)
        VALUES (NEW.id_vendeur, NEW.id_recipe, v_total * v_rule.percent, NEW.sell_date);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Créer le trigger pour la table recipe_sell
CREATE OR REPLACE TRIGGER trigger_calculate_commission
AFTER INSERT ON recipe_sell
FOR EACH ROW
EXECUTE FUNCTION calculate_commission();




-- Historique des prix : une ligne par changement de prix, datée du jour du changement
CREATE OR REPLACE FUNCTION log_price_change()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO recipe_price_history (id_recipe, price_before, price_after, change_date)
    VALUES (NEW.id_recipe, OLD.price, NEW.price, CURRENT_DATE);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Création du trigger
CREATE OR REPLACE TRIGGER trg_log_price_change
AFTER UPDATE OF price ON recipe
FOR EACH ROW
WHEN (OLD.price IS DISTINCT FROM NEW.price) -- Ne se déclenche que si le prix change
EXECUTE FUNCTION log_price_change();
