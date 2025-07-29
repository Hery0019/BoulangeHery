-- 006 — Contraintes d'unicité (noms identiques à ceux générés par 1-schema.sql).
-- Le script ÉCHOUE volontairement si des doublons existent : les repérer avec les
-- requêtes en commentaire, les corriger, puis rejouer. Rejouable.
--
--   SELECT email, count(*) FROM gotta_taste_user GROUP BY 1 HAVING count(*) > 1;
--   SELECT category_name, count(*) FROM category GROUP BY 1 HAVING count(*) > 1;
--   SELECT perfume_name, count(*) FROM perfume GROUP BY 1 HAVING count(*) > 1;
--   SELECT email, count(*) FROM vendeur GROUP BY 1 HAVING count(*) > 1;
--   SELECT ingredient_name, unit, count(*) FROM ingredient GROUP BY 1, 2 HAVING count(*) > 1;
--   SELECT id_recipe, step_number, count(*) FROM step GROUP BY 1, 2 HAVING count(*) > 1;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'gotta_taste_user_email_key') THEN
        ALTER TABLE gotta_taste_user ADD CONSTRAINT gotta_taste_user_email_key UNIQUE (email);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'category_category_name_key') THEN
        ALTER TABLE category ADD CONSTRAINT category_category_name_key UNIQUE (category_name);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'perfume_perfume_name_key') THEN
        ALTER TABLE perfume ADD CONSTRAINT perfume_perfume_name_key UNIQUE (perfume_name);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'vendeur_email_key') THEN
        ALTER TABLE vendeur ADD CONSTRAINT vendeur_email_key UNIQUE (email);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ingredient_ingredient_name_unit_key') THEN
        ALTER TABLE ingredient ADD CONSTRAINT ingredient_ingredient_name_unit_key UNIQUE (ingredient_name, unit);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'step_id_recipe_step_number_key') THEN
        ALTER TABLE step ADD CONSTRAINT step_id_recipe_step_number_key UNIQUE (id_recipe, step_number);
    END IF;
END $$;
