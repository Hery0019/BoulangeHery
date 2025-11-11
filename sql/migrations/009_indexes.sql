-- 009 — Index sur les colonnes filtrées et jointes en permanence.
--
-- Le schéma ne comportait que les index implicites des clés primaires et des
-- contraintes d'unicité : aucune clé étrangère n'était indexée. Chaque vente
-- lue par vendeur, chaque commission filtrée par période et chaque suppression
-- vérifiant ses références balayaient la table entière. Rejouable.

-- Recettes : filtres de la recherche multicritère
CREATE INDEX IF NOT EXISTS idx_recipe_category ON recipe (id_category);
CREATE INDEX IF NOT EXISTS idx_recipe_perfume ON recipe (id_perfume);

-- Composition : le sens id_ingredient -> recettes n'est pas couvert par la clé primaire
CREATE INDEX IF NOT EXISTS idx_recipe_ingredient_ingredient ON recipe_ingredient (id_ingredient);

-- Avis
CREATE INDEX IF NOT EXISTS idx_review_recipe ON review (id_recipe);
CREATE INDEX IF NOT EXISTS idx_review_user ON review (id_user);
CREATE INDEX IF NOT EXISTS idx_review_date ON review (review_date);

-- Ventes : listées et agrégées par recette, vendeur et période
CREATE INDEX IF NOT EXISTS idx_recipe_sell_recipe ON recipe_sell (id_recipe);
CREATE INDEX IF NOT EXISTS idx_recipe_sell_vendeur ON recipe_sell (id_vendeur);
CREATE INDEX IF NOT EXISTS idx_recipe_sell_category ON recipe_sell (id_category);
CREATE INDEX IF NOT EXISTS idx_recipe_sell_user ON recipe_sell (id_user);
CREATE INDEX IF NOT EXISTS idx_recipe_sell_date ON recipe_sell (sell_date);

-- Commissions : la clé étrangère vers la vente sert au recalcul par trigger
CREATE INDEX IF NOT EXISTS idx_commission_sell ON commission (id_recipe_sell);
CREATE INDEX IF NOT EXISTS idx_commission_vendeur ON commission (id_vendeur);
CREATE INDEX IF NOT EXISTS idx_commission_recipe ON commission (id_recipe);
CREATE INDEX IF NOT EXISTS idx_commission_date ON commission (commission_date);

-- Règle de commission : recherche de la plus récente antérieure à la vente
CREATE INDEX IF NOT EXISTS idx_commission_change_date ON commission_change (commission_change_date);

-- Historique des prix
CREATE INDEX IF NOT EXISTS idx_price_history_recipe ON recipe_price_history (id_recipe);
CREATE INDEX IF NOT EXISTS idx_price_history_date ON recipe_price_history (change_date);
