-- 004 — Mots de passe hachés (PBKDF2, voir util.PasswordHasher).
-- 1) Élargir la colonne (l'empreinte fait ~90 caractères).
-- 2) Convertir les mots de passe encore en clair :
--        java -cp "WEB-INF/classes;WEB-INF/lib/*" tools.UserAdmin rehash
--    Tant que ce n'est pas fait, les comptes concernés ne peuvent plus se connecter
--    (un mot de passe en clair n'est jamais accepté par User.authenticate).
-- Rejouable.

ALTER TABLE gotta_taste_user ALTER COLUMN user_password TYPE VARCHAR(255);
