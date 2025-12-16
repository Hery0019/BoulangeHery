DROP DATABASE IF EXISTS gotta_taste;
CREATE DATABASE gotta_taste;

\c gotta_taste;

CREATE TABLE gotta_taste_user (
    id_user SERIAL PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    user_password VARCHAR(255) NOT NULL, -- empreinte PBKDF2 (util.PasswordHasher), jamais en clair
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN'
        CHECK (role IN ('ADMIN', 'BOULANGER', 'VENDEUR'))
);

CREATE TABLE category (
    id_category SERIAL PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE perfume (
    id_perfume SERIAL PRIMARY KEY,
    perfume_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE recipe (
    id_recipe SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    recipe_description TEXT,
    id_category INT NOT NULL,
    id_perfume INT NOT NULL,
    cook_time TIME NOT NULL,
    id_created_by INT, -- compte auteur ; NULL si le compte a été supprimé
    created_by VARCHAR(255), -- auteur en texte libre, conservé pour les recettes non rattachées
    created_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    price DECIMAL(10,2) NOT NULL,
    picture VARCHAR(255),
    FOREIGN KEY (id_category) REFERENCES category(id_category) ON DELETE RESTRICT,
    FOREIGN KEY (id_perfume) REFERENCES perfume(id_perfume) ON DELETE RESTRICT,
    FOREIGN KEY (id_created_by) REFERENCES gotta_taste_user(id_user) ON DELETE SET NULL
);


CREATE TABLE vendeur (
    id_vendeur  SERIAL PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    sexe VARCHAR(10) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    salary DECIMAL(10,2) NOT NULL
);


CREATE TABLE commission_change (
    id_commission_change SERIAL PRIMARY KEY,
    percent  DECIMAL(10,2) NOT NULL,
    commission_change_value DECIMAL(10,2) NOT NULL,
    commission_change_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ingredient (
    id_ingredient SERIAL PRIMARY KEY,
    ingredient_name VARCHAR(255) NOT NULL,
    unit VARCHAR(50) NOT NULL, -- For example, grams, milliliters, teaspoons, etc.
    price NUMERIC(10,2) NOT NULL DEFAULT 0, -- prix unitaire (INT arrondissait 1.5 en 2 et 0.02 en 0)
    UNIQUE (ingredient_name, unit)
);

CREATE TABLE recipe_ingredient (
    id_recipe INT,
    id_ingredient INT,
    quantity DECIMAL(10,2), -- To store the amount needed for each recipe
    PRIMARY KEY (id_recipe, id_ingredient),
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE CASCADE,
    FOREIGN KEY (id_ingredient) REFERENCES ingredient(id_ingredient) ON DELETE RESTRICT
);

CREATE TABLE step (
    id_step SERIAL PRIMARY KEY,
    id_recipe INT NOT NULL,
    step_number INT NOT NULL,
    instruction TEXT NOT NULL,
    cook_time TIME NOT NULL,
    UNIQUE (id_recipe, step_number),
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE CASCADE
);

CREATE TABLE review (
    id_review SERIAL PRIMARY KEY,
    id_user INT NOT NULL,
    id_recipe INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    review_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_user) REFERENCES gotta_taste_user(id_user) ON DELETE RESTRICT,
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE CASCADE
);

CREATE TABLE recipe_sell (
    id_recipe_sell SERIAL PRIMARY KEY,
    id_vendeur INT NOT NULL,
    id_recipe INT NOT NULL,
    id_category INT NOT NULL,
    id_user INT NOT NULL,
    combien INT NOT NULL,
    argent DECIMAL(10,2) NOT NULL,
    reste DECIMAL(10,2) NOT NULL CHECK (reste >= 0),
    sell_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE RESTRICT,
    FOREIGN KEY (id_vendeur) REFERENCES vendeur(id_vendeur) ON DELETE RESTRICT,
    FOREIGN KEY (id_category) REFERENCES category(id_category) ON DELETE RESTRICT,
    FOREIGN KEY (id_user) REFERENCES gotta_taste_user(id_user) ON DELETE RESTRICT
);

CREATE TABLE commission (
    id_commission SERIAL PRIMARY KEY,
    id_recipe_sell INT, -- vente d'origine (NULL pour les commissions antérieures à la migration 005)
    id_vendeur INT NOT NULL,
    id_recipe INT NOT NULL,
    commission_amount DECIMAL(10,2) NOT NULL,
    commission_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE RESTRICT,
    FOREIGN KEY (id_vendeur) REFERENCES vendeur(id_vendeur) ON DELETE RESTRICT,
    FOREIGN KEY (id_recipe_sell) REFERENCES recipe_sell(id_recipe_sell) ON DELETE CASCADE
);

CREATE TABLE recipe_stock (
    id_recipe_stock SERIAL PRIMARY KEY,
    id_recipe INT NOT NULL UNIQUE,
    reste INT NOT NULL CHECK (reste >= 0),
    seuil_alerte INT NOT NULL DEFAULT 0 CHECK (seuil_alerte >= 0), -- 0 = pas d'alerte
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE CASCADE
);

-- Sorties de stock constatées hors vente : invendus, casse, péremption, dons.
CREATE TABLE recipe_loss (
    id_recipe_loss SERIAL PRIMARY KEY,
    id_recipe INT NOT NULL,
    id_user INT, -- auteur du constat ; NULL si le compte est supprimé
    quantity INT NOT NULL CHECK (quantity > 0),
    reason VARCHAR(20) NOT NULL CHECK (reason IN ('INVENDU', 'CASSE', 'PERIME', 'OFFERT')),
    loss_date DATE NOT NULL DEFAULT CURRENT_DATE,
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE CASCADE,
    FOREIGN KEY (id_user) REFERENCES gotta_taste_user(id_user) ON DELETE SET NULL
);

CREATE TABLE ingredient_stock (
    id_ingredient INT PRIMARY KEY REFERENCES ingredient(id_ingredient) ON DELETE CASCADE,
    reste NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (reste >= 0) -- matière première disponible
);

CREATE TABLE production (
    id_production SERIAL PRIMARY KEY,
    id_recipe INT NOT NULL,
    id_user INT, -- auteur de l'ordre ; NULL si le compte est supprimé
    quantity INT NOT NULL CHECK (quantity > 0),
    production_date DATE NOT NULL DEFAULT CURRENT_DATE,
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE RESTRICT,
    FOREIGN KEY (id_user) REFERENCES gotta_taste_user(id_user) ON DELETE SET NULL
);

CREATE TABLE recipe_price_history (
    id_recipe_price_history SERIAL PRIMARY KEY,
    id_recipe INT NOT NULL,
    price_before DECIMAL(10,2) NOT NULL,
    price_after DECIMAL(10,2) NOT NULL,
    change_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE CASCADE
);


-- Coût matière d'une recette (vue : suit les prix des ingrédients et la composition).
CREATE OR REPLACE VIEW recipe_cost AS
SELECT r.id_recipe,
       COALESCE(SUM(ri.quantity * i.price), 0)::NUMERIC(12,2) AS cost
FROM recipe r
LEFT JOIN recipe_ingredient ri ON ri.id_recipe = r.id_recipe
LEFT JOIN ingredient i ON i.id_ingredient = ri.id_ingredient
GROUP BY r.id_recipe;

-- Index sur les colonnes filtrées et jointes en permanence (voir migration 009).
-- Recettes : filtres de la recherche multicritère
CREATE INDEX IF NOT EXISTS idx_recipe_category ON recipe (id_category);
CREATE INDEX IF NOT EXISTS idx_recipe_perfume ON recipe (id_perfume);
CREATE INDEX IF NOT EXISTS idx_recipe_created_by ON recipe (id_created_by);

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

-- Production
CREATE INDEX IF NOT EXISTS idx_production_recipe ON production (id_recipe);
CREATE INDEX IF NOT EXISTS idx_production_date ON production (production_date);

-- Pertes
CREATE INDEX IF NOT EXISTS idx_recipe_loss_recipe ON recipe_loss (id_recipe);
CREATE INDEX IF NOT EXISTS idx_recipe_loss_date ON recipe_loss (loss_date);
