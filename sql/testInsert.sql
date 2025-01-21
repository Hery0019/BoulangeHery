-- Ajouter une nouvelle recette
INSERT INTO recipe (title, recipe_description, id_category, cook_time, created_by) 
VALUES ('Pain Viennois', 'Un pain moelleux et leger, parfait pour le petit-dejeuner.', 1, '01:00:00', 'Marie Boulanger');

-- Obtenir l'identifiant de la recette ajoutée
-- (Remplacez cette ligne par une récupération manuelle si nécessaire)
-- SELECT currval('recipe_id_seq');

-- Ajouter les ingrédients de la recette
INSERT INTO recipe_ingredient (id_recipe, id_ingredient, quantity) 
VALUES 
    (LASTVAL(), 1, 500), -- Farine
    (LASTVAL(), 2, 100), -- Beurre
    (LASTVAL(), 3, 50),  -- Sucre
    (LASTVAL(), 4, 15);  -- Levure boulangere

-- Ajouter les étapes de préparation
INSERT INTO step (id_recipe, step_number, instruction) 
VALUES 
    (LASTVAL(), 1, 'Melanger la farine, le sucre, la levure et le beurre fondu.'),
    (LASTVAL(), 2, 'Laisser reposer la pate pendant 1 heure.'),
    (LASTVAL(), 3, 'Faconner en petits pains et cuire au four a 200C pendant 20 minutes.');

-- Ajouter un avis utilisateur (optionnel)
INSERT INTO review (id_user, id_recipe, rating, comment, review_date) 
VALUES (1, LASTVAL(), 5, 'Tres bon pain viennois, doux et leger.', '2025-01-07');
