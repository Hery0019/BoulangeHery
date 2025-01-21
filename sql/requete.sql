SELECT DISTINCT r.*
FROM recipe r
JOIN recipe_ingredient ri ON r.id_recipe = ri.id_recipe
JOIN ingredient i ON ri.id_ingredient = i.id_ingredient
WHERE i.ingredient_name IN ('Beurre', 'Farine', 'Sucre');

SELECT * FROM recipe_sell WHERE 1=1 AND combien >= 0 AND combien <= 9 AND argent >= 0 AND argent <= 10AND reste >= ? AND reste <= ? ORDER BY id_recipe_sell ASC

 SELECT commission_change_value FROM commission_change WHERE commission_change_date <= '2025-01-23' ORDER BY commission_change_date DESC LIMIT 1;