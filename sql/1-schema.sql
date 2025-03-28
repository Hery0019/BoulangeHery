DROP DATABASE IF EXISTS gotta_taste;
CREATE DATABASE gotta_taste;

\c gotta_taste;

CREATE TABLE gotta_taste_user (
    id_user SERIAL PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    user_password VARCHAR(100) NOT NULL
);

CREATE TABLE category (
    id_category SERIAL PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL
);

CREATE TABLE perfume (
    id_perfume SERIAL PRIMARY KEY,
    perfume_name VARCHAR(255) NOT NULL
);

CREATE TABLE recipe (
    id_recipe SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    recipe_description TEXT,
    id_category INT NOT NULL,
    id_perfume INT NOT NULL,
    cook_time TIME NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    price DECIMAL(10,2) NOT NULL,
    picture VARCHAR(255),
    FOREIGN KEY (id_category) REFERENCES category(id_category),
    FOREIGN KEY (id_perfume) REFERENCES perfume(id_perfume)
);


CREATE TABLE vendeur (
    id_vendeur  SERIAL PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    sexe VARCHAR(10) NOT NULL,
    email VARCHAR(100) NOT NULL,
    salary DECIMAL(10,2) NOT NULL
);

CREATE TABLE commission (
    id_commission SERIAL PRIMARY KEY,
    id_vendeur INT NOT NULL,
    id_recipe INT NOT NULL,
    commission_amount DECIMAL(10,2) NOT NULL,
    commission_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe),
    FOREIGN KEY (id_vendeur) REFERENCES vendeur(id_vendeur)
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
    price INT NOT NULL DEFAULT 0
);

CREATE TABLE recipe_ingredient (
    id_recipe INT,
    id_ingredient INT,
    quantity DECIMAL(10,2), -- To store the amount needed for each recipe
    PRIMARY KEY (id_recipe, id_ingredient),
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe),
    FOREIGN KEY (id_ingredient) REFERENCES ingredient(id_ingredient)
);

CREATE TABLE step (
    id_step SERIAL PRIMARY KEY,
    id_recipe INT NOT NULL,
    step_number INT NOT NULL,
    instruction TEXT NOT NULL,
    cook_time TIME NOT NULL,
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe)
);

CREATE TABLE review (
    id_review SERIAL PRIMARY KEY,
    id_user INT NOT NULL,
    id_recipe INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    review_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_user) REFERENCES gotta_taste_user(id_user),
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe)
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
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe),
    FOREIGN KEY (id_vendeur) REFERENCES vendeur(id_vendeur),
    FOREIGN KEY (id_category) REFERENCES category(id_category),
    FOREIGN KEY (id_user) REFERENCES gotta_taste_user(id_user)
);

CREATE TABLE recipe_stock (
    id_recipe_stock SERIAL PRIMARY KEY,
    id_recipe INT NOT NULL,
    reste INT NOT NULL CHECK (reste >= 0),
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe)
);

CREATE TABLE recipe_price_history (
    id_recipe_price_history SERIAL PRIMARY KEY,
    id_recipe INT NOT NULL,
    price_before DECIMAL(10,2) NOT NULL,
    price_after DECIMAL(10,2) NOT NULL,
    change_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe)
);

