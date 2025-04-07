-- 003 — L'historique des prix est daté du jour du changement (CURRENT_DATE)
-- et non de la date de création de la recette. Rejouable.

CREATE OR REPLACE FUNCTION log_price_change()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO recipe_price_history (id_recipe, price_before, price_after, change_date)
    VALUES (NEW.id_recipe, OLD.price, NEW.price, CURRENT_DATE);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
