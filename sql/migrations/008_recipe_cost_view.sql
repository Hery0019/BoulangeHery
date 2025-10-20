-- 008 — Coût matière d'une recette.
--
-- ingredient.price (prix unitaire) et recipe_ingredient.quantity existaient
-- déjà mais n'étaient jamais multipliés : impossible de savoir ce que coûte
-- une recette, donc ce qu'elle rapporte.
--
-- Une vue plutôt qu'une colonne : le coût suit automatiquement un changement
-- de prix d'ingrédient ou de composition, sans trigger ni risque d'écart.
-- Rejouable.

CREATE OR REPLACE VIEW recipe_cost AS
SELECT r.id_recipe,
       COALESCE(SUM(ri.quantity * i.price), 0)::NUMERIC(12,2) AS cost
FROM recipe r
LEFT JOIN recipe_ingredient ri ON ri.id_recipe = r.id_recipe
LEFT JOIN ingredient i ON i.id_ingredient = ri.id_ingredient
GROUP BY r.id_recipe;

COMMENT ON VIEW recipe_cost IS
    'Coût matière par recette : somme des quantités multipliées par le prix unitaire des ingrédients.';
