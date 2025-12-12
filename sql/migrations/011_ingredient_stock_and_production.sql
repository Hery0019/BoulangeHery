-- 011 — Stock de matières premières et ordres de production.
--
-- Le stock ne concernait que le produit fini, approvisionné à la main : rien
-- ne reliait « fabriquer 200 baguettes » à la farine que cela consomme.
--
--   ingredient_stock : une ligne par ingrédient, créée avec lui
--   production       : un ordre de fabrication, qui consomme les ingrédients
--                      de la recette et alimente recipe_stock
--
-- Une production est un événement constaté : elle ne se modifie pas et ne se
-- supprime pas, comme une sortie de matière réelle. Rejouable.

CREATE TABLE IF NOT EXISTS ingredient_stock (
    id_ingredient INT PRIMARY KEY REFERENCES ingredient(id_ingredient) ON DELETE CASCADE,
    reste NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (reste >= 0)
);

CREATE TABLE IF NOT EXISTS production (
    id_production SERIAL PRIMARY KEY,
    id_recipe INT NOT NULL REFERENCES recipe(id_recipe) ON DELETE RESTRICT,
    id_user INT REFERENCES gotta_taste_user(id_user) ON DELETE SET NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    production_date DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX IF NOT EXISTS idx_production_recipe ON production (id_recipe);
CREATE INDEX IF NOT EXISTS idx_production_date ON production (production_date);

-- Les ingrédients déjà présents reçoivent leur ligne de stock, à zéro.
INSERT INTO ingredient_stock (id_ingredient, reste)
SELECT id_ingredient, 0 FROM ingredient
ON CONFLICT (id_ingredient) DO NOTHING;

\ir '../2-trigger.sql'
