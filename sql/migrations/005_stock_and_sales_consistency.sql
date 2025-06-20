-- 005 — Stock et ventes cohérents.
--   * recipe_stock : une seule ligne par recette (UNIQUE), créée à 0 avec la recette (trigger),
--     décrément atomique ; modification/suppression d'une vente restitue le stock.
--   * commission liée à sa vente (id_recipe_sell, ON DELETE CASCADE) et recalculée si la vente change.
--   * cook_time recalculé aussi à la suppression d'une étape.
-- Rejouable. Exécuter avec psql (le ir final recharge sql/2-trigger.sql, chemin relatif à ce fichier).
-- Ce script ajoute les changements de schéma, 2-trigger.sql porte les fonctions.

-- Doublons éventuels de stock : on garde la ligne la plus ancienne
DELETE FROM recipe_stock a
USING recipe_stock b
WHERE a.id_recipe = b.id_recipe AND a.id_recipe_stock > b.id_recipe_stock;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'recipe_stock_id_recipe_key') THEN
        ALTER TABLE recipe_stock ADD CONSTRAINT recipe_stock_id_recipe_key UNIQUE (id_recipe);
    END IF;
END $$;

-- Recettes sans stock : ligne à 0
INSERT INTO recipe_stock (id_recipe, reste)
SELECT r.id_recipe, 0 FROM recipe r
WHERE NOT EXISTS (SELECT 1 FROM recipe_stock s WHERE s.id_recipe = r.id_recipe);

ALTER TABLE commission ADD COLUMN IF NOT EXISTS id_recipe_sell INT
    REFERENCES recipe_sell(id_recipe_sell) ON DELETE CASCADE;

-- Fonctions et triggers : même contenu que sql/2-trigger.sql
\i 2-trigger.sql
