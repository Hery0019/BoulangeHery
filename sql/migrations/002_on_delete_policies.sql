-- 002 — Politiques ON DELETE explicites.
--   CASCADE  : les composants d'une recette (ingrédients, étapes, avis, stock, historique des prix)
--              disparaissent avec elle.
--   RESTRICT : les référentiels (catégorie, parfum, ingrédient, vendeur, utilisateur) et les faits
--              comptables (ventes, commissions) empêchent la suppression de ce qu'ils référencent.
-- Rejouable : chaque contrainte est supprimée puis recréée.

ALTER TABLE recipe
    DROP CONSTRAINT IF EXISTS recipe_id_category_fkey,
    ADD CONSTRAINT recipe_id_category_fkey FOREIGN KEY (id_category) REFERENCES category(id_category) ON DELETE RESTRICT,
    DROP CONSTRAINT IF EXISTS recipe_id_perfume_fkey,
    ADD CONSTRAINT recipe_id_perfume_fkey FOREIGN KEY (id_perfume) REFERENCES perfume(id_perfume) ON DELETE RESTRICT;

ALTER TABLE recipe_ingredient
    DROP CONSTRAINT IF EXISTS recipe_ingredient_id_recipe_fkey,
    ADD CONSTRAINT recipe_ingredient_id_recipe_fkey FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE CASCADE,
    DROP CONSTRAINT IF EXISTS recipe_ingredient_id_ingredient_fkey,
    ADD CONSTRAINT recipe_ingredient_id_ingredient_fkey FOREIGN KEY (id_ingredient) REFERENCES ingredient(id_ingredient) ON DELETE RESTRICT;

ALTER TABLE step
    DROP CONSTRAINT IF EXISTS step_id_recipe_fkey,
    ADD CONSTRAINT step_id_recipe_fkey FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE CASCADE;

ALTER TABLE review
    DROP CONSTRAINT IF EXISTS review_id_recipe_fkey,
    ADD CONSTRAINT review_id_recipe_fkey FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE CASCADE,
    DROP CONSTRAINT IF EXISTS review_id_user_fkey,
    ADD CONSTRAINT review_id_user_fkey FOREIGN KEY (id_user) REFERENCES gotta_taste_user(id_user) ON DELETE RESTRICT;

ALTER TABLE recipe_stock
    DROP CONSTRAINT IF EXISTS recipe_stock_id_recipe_fkey,
    ADD CONSTRAINT recipe_stock_id_recipe_fkey FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE CASCADE;

ALTER TABLE recipe_price_history
    DROP CONSTRAINT IF EXISTS recipe_price_history_id_recipe_fkey,
    ADD CONSTRAINT recipe_price_history_id_recipe_fkey FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE CASCADE;

ALTER TABLE recipe_sell
    DROP CONSTRAINT IF EXISTS recipe_sell_id_recipe_fkey,
    ADD CONSTRAINT recipe_sell_id_recipe_fkey FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE RESTRICT,
    DROP CONSTRAINT IF EXISTS recipe_sell_id_vendeur_fkey,
    ADD CONSTRAINT recipe_sell_id_vendeur_fkey FOREIGN KEY (id_vendeur) REFERENCES vendeur(id_vendeur) ON DELETE RESTRICT,
    DROP CONSTRAINT IF EXISTS recipe_sell_id_category_fkey,
    ADD CONSTRAINT recipe_sell_id_category_fkey FOREIGN KEY (id_category) REFERENCES category(id_category) ON DELETE RESTRICT,
    DROP CONSTRAINT IF EXISTS recipe_sell_id_user_fkey,
    ADD CONSTRAINT recipe_sell_id_user_fkey FOREIGN KEY (id_user) REFERENCES gotta_taste_user(id_user) ON DELETE RESTRICT;

ALTER TABLE commission
    DROP CONSTRAINT IF EXISTS commission_id_recipe_fkey,
    ADD CONSTRAINT commission_id_recipe_fkey FOREIGN KEY (id_recipe) REFERENCES recipe(id_recipe) ON DELETE RESTRICT,
    DROP CONSTRAINT IF EXISTS commission_id_vendeur_fkey,
    ADD CONSTRAINT commission_id_vendeur_fkey FOREIGN KEY (id_vendeur) REFERENCES vendeur(id_vendeur) ON DELETE RESTRICT;
