-- 001 — Trigger commission : règle en vigueur À la date de vente (<=) au lieu de >=,
-- lecture unique de la règle, pas de commission si aucune règle n'est applicable.
-- Idempotent : peut être rejoué.

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
