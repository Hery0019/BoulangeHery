-- 007 — ingredient.price en NUMERIC(10,2) : la colonne INT arrondissait silencieusement
-- les prix unitaires (1.5 -> 2, 0.02 -> 0). Rejouable.

ALTER TABLE ingredient ALTER COLUMN price TYPE NUMERIC(10,2);
