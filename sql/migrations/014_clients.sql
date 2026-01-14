-- 014 — Clients des ventes.
--
-- recipe_sell.id_user désignait la colonne « Client » de l'écran des ventes,
-- mais pointait vers gotta_taste_user : les clients affichés étaient en réalité
-- les comptes de l'application, c'est-à-dire les employés.
--
-- La colonne existante retrouve son sens — l'employé qui a saisi la vente — et
-- une table client accueille les vrais acheteurs. Une vente sans client reste
-- possible : c'est une vente au comptoir. Rejouable.

CREATE TABLE IF NOT EXISTS client (
    id_client SERIAL PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(100) UNIQUE,
    created_date DATE NOT NULL DEFAULT CURRENT_DATE
);

ALTER TABLE recipe_sell
    ADD COLUMN IF NOT EXISTS id_client INT REFERENCES client(id_client) ON DELETE RESTRICT;

CREATE INDEX IF NOT EXISTS idx_recipe_sell_client ON recipe_sell (id_client);

COMMENT ON COLUMN recipe_sell.id_user IS 'Compte qui a saisi la vente';
COMMENT ON COLUMN recipe_sell.id_client IS 'Acheteur ; NULL pour une vente au comptoir';
