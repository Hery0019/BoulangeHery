-- 013 — L'auteur d'une recette est un compte, plus une chaîne saisie à la main.
--
-- recipe.created_by était un VARCHAR libre : deux orthographes du même nom
-- faisaient deux auteurs, et rien ne reliait une recette au compte qui l'avait
-- créée. La colonne devient une clé étrangère ; l'ancien texte est conservé
-- pour les lignes qu'on ne sait pas rattacher, et sert alors d'affichage.
-- Rejouable.

ALTER TABLE recipe
    ADD COLUMN IF NOT EXISTS id_created_by INT REFERENCES gotta_taste_user(id_user) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_recipe_created_by ON recipe (id_created_by);

-- Rattachement des recettes existantes par nom complet « Prénom Nom ».
UPDATE recipe r
SET id_created_by = u.id_user
FROM gotta_taste_user u
WHERE r.id_created_by IS NULL
  AND lower(btrim(r.created_by)) = lower(u.firstname || ' ' || u.lastname);

-- Une recette rattachée n'a plus besoin de son texte ; les autres le gardent.
ALTER TABLE recipe ALTER COLUMN created_by DROP NOT NULL;
UPDATE recipe SET created_by = NULL WHERE id_created_by IS NOT NULL;
