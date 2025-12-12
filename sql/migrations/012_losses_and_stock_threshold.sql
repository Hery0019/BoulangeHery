-- 012 — Pertes (invendus, casse, péremption) et seuil d'alerte de stock.
--
-- Le stock ne diminuait que par les ventes : ce qui part à la poubelle en fin
-- de journée restait compté comme disponible. Le taux d'invendus est pourtant
-- l'indicateur que regarde une boulangerie tous les soirs.
--
-- recipe_stock reçoit en outre un seuil d'alerte, pour signaler les recettes à
-- réapprovisionner avant la rupture. Rejouable.

ALTER TABLE recipe_stock
    ADD COLUMN IF NOT EXISTS seuil_alerte INT NOT NULL DEFAULT 0 CHECK (seuil_alerte >= 0);

CREATE TABLE IF NOT EXISTS recipe_loss (
    id_recipe_loss SERIAL PRIMARY KEY,
    id_recipe INT NOT NULL REFERENCES recipe(id_recipe) ON DELETE CASCADE,
    id_user INT REFERENCES gotta_taste_user(id_user) ON DELETE SET NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    reason VARCHAR(20) NOT NULL CHECK (reason IN ('INVENDU', 'CASSE', 'PERIME', 'OFFERT')),
    loss_date DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX IF NOT EXISTS idx_recipe_loss_recipe ON recipe_loss (id_recipe);
CREATE INDEX IF NOT EXISTS idx_recipe_loss_date ON recipe_loss (loss_date);

\ir '../2-trigger.sql'
