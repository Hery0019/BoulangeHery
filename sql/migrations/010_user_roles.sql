-- 010 — Rôles des comptes.
--
-- L'accès était binaire : tout compte connecté pouvait supprimer une recette
-- comme consulter l'intégralité des commissions. Trois rôles séparent
-- désormais le catalogue de la caisse.
--
--   ADMIN     : tout
--   BOULANGER : recettes, étapes, ingrédients, catégories, stock
--   VENDEUR   : ventes et commissions
--
-- Les comptes existants deviennent ADMIN pour ne rien casser : les rétrograder
-- ensuite avec « tools.UserAdmin role <email> <rôle> ». Rejouable.

ALTER TABLE gotta_taste_user
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'ADMIN';

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'gotta_taste_user_role_check') THEN
        ALTER TABLE gotta_taste_user ADD CONSTRAINT gotta_taste_user_role_check
            CHECK (role IN ('ADMIN', 'BOULANGER', 'VENDEUR'));
    END IF;
END $$;
