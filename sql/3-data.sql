-- Vider les tables existantes
TRUNCATE TABLE category, gotta_taste_user, recipe, recipe_ingredient, recipe_sell, vendeur, commission, commission_change, ingredient, step, review, perfume RESTART IDENTITY CASCADE;

-- Inserer des categories pour une boulangerie
INSERT INTO category (category_name) VALUES
    ('Pains'), ('Viennoiseries'), ('Patisseries');

-- Inserer des utilisateurs fictifs
INSERT INTO gotta_taste_user (firstname, lastname, email, user_password) VALUES  
    ('Marie', 'Boulanger', 'marie.boulanger@example.com', 'pain123'),
    ('Paul', 'Patissier', 'paul.patissier@example.com', 'croissant456'),
    ('Hery', 'RAKOTONARIVO', 'herakotonarivo@gmail.com', '123');

-- Inserer des parfums
INSERT INTO perfume (perfume_name) VALUES
    ('Nature'), ('Chocolat'), ('Pomme');

-- Inserer des ingredients utilises en boulangerie
INSERT INTO ingredient (ingredient_name, unit, price) VALUES  
    ('Farine', 'gramme', 1.5),
    ('Beurre', 'gramme', 10),
    ('Sucre', 'gramme', 2),
    ('Levure boulangere', 'gramme', 0.02),
    ('Lait', 'litre', 1),
    ('Chocolat', 'gramme', 12),
    ('Pommes', 'gramme', 3),
    ('Creme patissiere', 'litre', 5);

-- Inserer des recettes de boulangerie avec des prix
INSERT INTO recipe (title, recipe_description, id_category, id_perfume, cook_time, created_by, created_date, price, picture) VALUES  
    ('Baguette Tradition', 'Une baguette classique a la croute doree.', 1, 1, '00:00:00', 'Marie Boulanger', '2024-01-30', 1500, 'assets/img/recipies/baguette.jpeg'),
    ('Croissant', 'Un croissant feuillete au beurre.', 2, 1, '00:00:00', 'Paul Patissier', '2025-01-12', 2500, 'assets/img/recipies/croissants.jpg'),
    ('Pain de Campagne', 'Un pain rustique au gout de tradition.', 1, 1, '00:00:00', 'Marie Boulanger', '2025-01-30', 2000, 'assets/img/recipies/pain-de-campagne.jpg'),
    ('Pain Complet', 'Un pain riche en fibres, parfait pour la sante.', 1, 1, '00:00:00', 'Marie Boulanger', '2025-01-03', 1800, 'assets/img/recipies/brioche-tressee.jpg'),
    ('Brioche Tressee', 'Une brioche moelleuse et sucree.', 2, 1, '00:00:00', 'Paul Patissier', '2024-12-11', 3000 , 'assets/img/recipies/brioche-tressee.jpg'),
    ('Pain au Chocolat', 'Une viennoiserie gourmande au chocolat.', 2, 2, '00:00:00', 'Paul Patissier', '2025-01-01', 2800, 'assets/img/recipies/pain-au-chocolat.jpeg'),
    ('Eclair au Chocolat', 'Un classique patissier garni de creme au chocolat.', 3, 2, '00:00:00', 'Paul Patissier', '2024-11-30', 3500, 'assets/img/recipies/eclair-choco.jpg'),
    ('Tarte aux Pommes', 'Une tarte delicieuse avec des pommes caramelisees.', 3, 3, '00:00:00', 'Marie Boulanger', '2025-01-19', 4000, 'assets/img/recipies/tarte-pomme.jpg');


-- Associer les ingredients aux recettes
INSERT INTO recipe_ingredient (id_recipe, id_ingredient, quantity) VALUES  
    (1, 1, 500), -- Farine pour la baguette
    (1, 4, 10),  -- Levure pour la baguette
    (2, 1, 300), -- Farine pour les croissants
    (2, 2, 200), -- Beurre pour les croissants
    (2, 3, 50),  -- Sucre pour les croissants
    (3, 1, 600), -- Farine pour le pain de campagne
    (3, 4, 15),  -- Levure pour le pain de campagne
    (4, 1, 400), -- Farine pour le pain complet
    (4, 3, 20),  -- Sucre pour le pain complet
    (5, 1, 500), -- Farine pour la brioche
    (5, 2, 250), -- Beurre pour la brioche
    (5, 3, 80),  -- Sucre pour la brioche
    (6, 1, 300), -- Farine pour le pain au chocolat
    (6, 2, 150), -- Beurre pour le pain au chocolat
    (6, 6, 200), -- Chocolat pour le pain au chocolat
    (7, 1, 250), -- Farine pour l eclair au chocolat
    (7, 6, 100), -- Chocolat pour l eclair
    (7, 8, 200), -- Creme patissiere pour l eclair
    (8, 1, 300), -- Farine pour la tarte aux pommes
    (8, 7, 500); -- Pommes pour la tarte aux pommes

-- Ajouter des etapes de preparation
INSERT INTO step (id_recipe, step_number, instruction, cook_time) VALUES  
    (1, 1, 'Melanger la farine, la levure et l eau.', '00:10:00'),
    (1, 2, 'Laisser reposer la pate.', '02:00:00'),
    (1, 3, 'Faconner en baguettes et cuire au four a 240C.', '00:25:00'),
    (2, 1, 'Preparer la detrempe.', '00:30:00'),
    (2, 2, 'Incorporer le beurre et laminer.', '01:00:00'),
    (2, 3, 'Former les croissants et cuire.', '00:25:00'),
    (3, 1, 'Melanger les ingredients.', '00:15:00'),
    (3, 2, 'Faconner en boule et cuire.', '00:45:00'),
    (4, 1, 'Melanger les ingredients.', '00:10:00'),
    (4, 2, 'Cuire le pain complet.', '00:35:00'),
    (5, 1, 'Preparer la pate.', '01:00:00'),
    (5, 2, 'Tresser et cuire.', '01:00:00'),
    (6, 1, 'Faconner et garnir de chocolat.', '00:45:00'),
    (6, 2, 'Cuire a 200C.', '01:00:00'),
    (7, 1, 'Preparer la pate a choux.', '00:45:00'),
    (7, 2, 'Garnir et glacer.', '00:45:00'),
    (8, 1, 'Preparer la pate.', '00:30:00'),
    (8, 2, 'Garnir et cuire.', '00:45:00');

-- Inserer des avis sur les recettes
INSERT INTO review (id_user, id_recipe, rating, comment, review_date) VALUES
    (1, 1, 5, 'Une baguette excellente et croustillante.', '2023-01-15'),
    (2, 2, 4, 'Un croissant savoureux mais un peu trop beurre.', '2023-02-10'),
    (3, 6, 5, 'Pain au chocolat delicieux, bien feuillete.', '2023-03-05'),
    (1, 8, 5, 'Tarte aux pommes fondante et bien caramelisee.', '2023-04-22'),
    (2, 5, 4, 'Une brioche moelleuse, mais un peu trop sucree.', '2023-05-18'),
    (3, 3, 5, 'Pain de campagne au goût authentique.', '2023-06-12'),
    (1, 4, 5, 'Pain complet parfait pour le petit-dejeuner.', '2023-07-08'),
    (2, 7, 4, 'Eclair au chocolat gourmand et genereux.', '2023-08-26'),
    (3, 1, 5, 'Baguette toujours aussi croustillante et savoureuse.', '2023-09-14'),
    (1, 2, 4, 'Croissant feuillete et bien beurre.', '2023-10-20'),
    (2, 6, 5, 'Un pain au chocolat fondant à souhait.', '2023-11-29'),
    (3, 8, 5, 'Tarte aux pommes delicieuse, bien doree.', '2023-12-17'),

    (1, 1, 5, 'La baguette parfaite pour accompagner un repas.', '2024-01-30'),
    (2, 2, 4, 'Croissant bon, mais un peu gras.', '2024-02-05'),
    (3, 6, 5, 'Un vrai regal, ce pain au chocolat !', '2024-03-10'),
    (1, 8, 5, 'Tarte aux pommes legèrement acidulee, un delice.', '2024-04-22'),
    (2, 5, 4, 'Brioche tendre et gourmande.', '2024-05-18'),
    (3, 3, 5, 'Pain de campagne avec une belle croûte.', '2024-06-12'),
    (1, 4, 5, 'Un pain complet qui tient bien au corps.', '2024-07-08'),
    (2, 7, 4, 'Eclair au chocolat excellent, juste sucre comme il faut.', '2024-08-26'),
    (3, 1, 5, 'Baguette toujours aussi savoureuse.', '2024-09-14'),
    (1, 2, 4, 'Croissant croustillant et aerien.', '2024-10-20'),
    (2, 6, 5, 'Pain au chocolat avec une belle couche de chocolat.', '2024-11-29'),
    (3, 8, 5, 'Tarte aux pommes bien equilibree en sucre.', '2024-12-17');


INSERT INTO commission_change (percent, commission_change_value, commission_change_date) VALUES
     (0.05, 200000, CURRENT_DATE);

INSERT INTO vendeur (firstname, lastname, sexe, email, salary) VALUES
    ('Alice', 'Dupont', 'F', 'alice.dupont@example.com', 2000.00),
    ('Bob', 'Martin', 'M', 'bob.martin@example.com', 1800.00),
    ('Kevin', 'Durant', 'M', 'kevin.durant@example.com', 2200.00),
    ('Jean', 'Francis', 'M', 'jean.francis@example.com', 1200.00),
    ('Maya', 'Moore', 'F', 'maya.moore@example.com', 2300.00),
    ('Natasha', 'Angel', 'F', 'nat.ange@example.com', 3200.00);

INSERT INTO recipe_stock (id_recipe, reste) VALUES 
    (1, 100),
    (2, 100),
    (3, 100),
    (4, 100),
    (5, 100),
    (6, 100),
    (7, 100),
    (8, 100);
