-- Scénario de vérification des règles portées par la base (triggers, contraintes).
-- À exécuter sur une base fraîchement installée avec 1-schema.sql, 2-trigger.sql, 3-data.sql :
--
--     psql -U postgres -d gotta_taste -v ON_ERROR_STOP=1 -f sql/tests/triggers_smoke_test.sql
--
-- Tout se passe dans une transaction annulée à la fin : la base n'est pas modifiée.
-- Chaque étape lève une exception (ASSERT) si le comportement attendu n'est pas observé ;
-- une exécution sans erreur affiche « OK » pour chaque règle.

\set ON_ERROR_STOP on
BEGIN;

DO $$
DECLARE
    v_reste      NUMERIC;
    v_sale       INT;
    v_commission NUMERIC;
    v_count      INT;
    v_recipe     INT;
    v_cook       TIME;
    v_history    RECORD;
    v_msg        TEXT;
BEGIN
    -- Données de démo attendues : recette 1 = Baguette 1500, recette 8 = Tarte 4000, stock 100 partout,
    -- règle de commission 5 % au-delà de 200 000 datée d'aujourd'hui.
    ASSERT (SELECT reste FROM recipe_stock WHERE id_recipe = 1) = 100, 'stock initial recette 1';
    ASSERT (SELECT count(*) FROM recipe_stock) = (SELECT count(*) FROM recipe), 'une ligne de stock par recette';
    RAISE NOTICE 'OK  stock initial : une ligne par recette';

    -- 1. Vente simple : reste calculé, stock décrémenté, pas de commission sous le seuil
    INSERT INTO recipe_sell (id_vendeur, id_recipe, id_category, id_user, combien, argent, reste, sell_date)
    VALUES (1, 1, 1, 1, 10, 20000, 0, CURRENT_DATE) RETURNING id_recipe_sell, reste INTO v_sale, v_reste;
    ASSERT v_reste = 5000, 'reste = argent - combien * prix (attendu 5000, obtenu ' || v_reste || ')';
    ASSERT (SELECT reste FROM recipe_stock WHERE id_recipe = 1) = 90, 'stock décrémenté de 10';
    ASSERT (SELECT count(*) FROM commission WHERE id_recipe_sell = v_sale) = 0, 'pas de commission sous 200 000';
    RAISE NOTICE 'OK  vente : reste, stock, seuil de commission';

    -- 2. Stock insuffisant : refus avec message explicite, stock inchangé
    BEGIN
        INSERT INTO recipe_sell (id_vendeur, id_recipe, id_category, id_user, combien, argent, reste, sell_date)
        VALUES (3, 8, 3, 1, 200, 900000, 0, CURRENT_DATE);
        RAISE EXCEPTION 'la vente de 200 unités aurait dû être refusée (stock 100)';
    EXCEPTION WHEN raise_exception THEN
        GET STACKED DIAGNOSTICS v_msg = MESSAGE_TEXT;
        ASSERT v_msg LIKE 'Stock insuffisant%', 'message stock insuffisant, obtenu : ' || v_msg;
    END;
    ASSERT (SELECT reste FROM recipe_stock WHERE id_recipe = 8) = 100, 'stock recette 8 inchangé après refus';
    RAISE NOTICE 'OK  stock insuffisant refusé sans effet';

    -- 3. Approvisionnement puis vente au-dessus du seuil : commission 5 % liée à la vente
    INSERT INTO recipe_stock (id_recipe, reste) VALUES (8, 150)
    ON CONFLICT (id_recipe) DO UPDATE SET reste = recipe_stock.reste + EXCLUDED.reste;
    INSERT INTO recipe_sell (id_vendeur, id_recipe, id_category, id_user, combien, argent, reste, sell_date)
    VALUES (3, 8, 3, 1, 200, 900000, 0, CURRENT_DATE) RETURNING id_recipe_sell INTO v_sale;
    ASSERT (SELECT reste FROM recipe_stock WHERE id_recipe = 8) = 50, 'stock 250 - 200 = 50';
    SELECT commission_amount INTO v_commission FROM commission WHERE id_recipe_sell = v_sale;
    ASSERT v_commission = 40000, 'commission 5 % de 800 000 (obtenu ' || COALESCE(v_commission::text, 'aucune') || ')';
    RAISE NOTICE 'OK  approvisionnement, commission calculée et liée à la vente';

    -- 4. Modification de la vente : stock réajusté, commission recalculée (une seule ligne)
    UPDATE recipe_sell SET combien = 100, argent = 500000 WHERE id_recipe_sell = v_sale;
    ASSERT (SELECT reste FROM recipe_stock WHERE id_recipe = 8) = 150, 'stock restitué puis reconsommé : 150';
    SELECT count(*), max(commission_amount) INTO v_count, v_commission FROM commission WHERE id_recipe_sell = v_sale;
    ASSERT v_count = 1 AND v_commission = 20000, 'commission recalculée à 20 000 en une ligne';
    RAISE NOTICE 'OK  modification de vente : stock et commission cohérents';

    -- 5. Suppression de la vente : stock restitué, commission supprimée
    DELETE FROM recipe_sell WHERE id_recipe_sell = v_sale;
    ASSERT (SELECT reste FROM recipe_stock WHERE id_recipe = 8) = 250, 'stock restitué : 250';
    ASSERT (SELECT count(*) FROM commission WHERE id_recipe_sell = v_sale) = 0, 'commission supprimée avec la vente';
    RAISE NOTICE 'OK  suppression de vente : stock et commission cohérents';

    -- 6. Argent insuffisant : contrainte recipe_sell_reste_check
    BEGIN
        INSERT INTO recipe_sell (id_vendeur, id_recipe, id_category, id_user, combien, argent, reste, sell_date)
        VALUES (1, 1, 1, 1, 1, 100, 0, CURRENT_DATE);
        RAISE EXCEPTION 'une vente payée 100 pour un prix de 1500 aurait dû être refusée';
    EXCEPTION WHEN check_violation THEN
        GET STACKED DIAGNOSTICS v_msg = CONSTRAINT_NAME;
        ASSERT v_msg = 'recipe_sell_reste_check', 'contrainte attendue recipe_sell_reste_check, obtenu ' || v_msg;
    END;
    ASSERT (SELECT reste FROM recipe_stock WHERE id_recipe = 1) = 90, 'stock inchangé après refus (argent)';
    RAISE NOTICE 'OK  argent insuffisant refusé';

    -- 7. Vente antérieure à toute règle de commission : acceptée, sans commission
    INSERT INTO recipe_sell (id_vendeur, id_recipe, id_category, id_user, combien, argent, reste, sell_date)
    VALUES (2, 8, 3, 1, 100, 400000, 0, CURRENT_DATE - 1) RETURNING id_recipe_sell INTO v_sale;
    ASSERT (SELECT count(*) FROM commission WHERE id_recipe_sell = v_sale) = 0, 'aucune règle applicable hier';
    RAISE NOTICE 'OK  vente sans règle applicable : pas de commission, pas de blocage';

    -- 8. Historique des prix daté du jour du changement
    UPDATE recipe SET price = 1600 WHERE id_recipe = 1;
    SELECT * INTO v_history FROM recipe_price_history WHERE id_recipe = 1 ORDER BY id_recipe_price_history DESC LIMIT 1;
    ASSERT v_history.price_before = 1500 AND v_history.price_after = 1600, 'prix avant/après';
    ASSERT v_history.change_date = CURRENT_DATE, 'date du changement = aujourd''hui';
    RAISE NOTICE 'OK  historique des prix';

    -- 9. Temps de préparation recalculé, y compris à la suppression d'une étape
    SELECT cook_time INTO v_cook FROM recipe WHERE id_recipe = 1;
    ASSERT v_cook = '02:35:00', 'somme des étapes de la baguette (obtenu ' || v_cook || ')';
    DELETE FROM step WHERE id_recipe = 1 AND step_number = 2;
    SELECT cook_time INTO v_cook FROM recipe WHERE id_recipe = 1;
    ASSERT v_cook = '00:35:00', 'temps recalculé après suppression d''étape (obtenu ' || v_cook || ')';
    RAISE NOTICE 'OK  temps de préparation';

    -- 10. Unicité
    BEGIN
        INSERT INTO category (category_name) VALUES ('Pains');
        RAISE EXCEPTION 'la catégorie en double aurait dû être refusée';
    EXCEPTION WHEN unique_violation THEN NULL;
    END;
    BEGIN
        INSERT INTO step (id_recipe, step_number, instruction, cook_time) VALUES (1, 1, 'doublon', '00:01:00');
        RAISE EXCEPTION 'l''étape en double aurait dû être refusée';
    EXCEPTION WHEN unique_violation THEN NULL;
    END;
    RAISE NOTICE 'OK  contraintes d''unicité';

    -- 11. Nouvelle recette : stock créé à 0 ; suppression en cascade des composants
    INSERT INTO recipe (title, recipe_description, id_category, id_perfume, cook_time, created_by, created_date, price)
    VALUES ('Test', 'temporaire', 1, 1, '00:00:00', 'test', CURRENT_DATE, 1000) RETURNING id_recipe INTO v_recipe;
    ASSERT (SELECT reste FROM recipe_stock WHERE id_recipe = v_recipe) = 0, 'stock créé à 0 avec la recette';
    INSERT INTO step (id_recipe, step_number, instruction, cook_time) VALUES (v_recipe, 1, 'x', '00:05:00');
    INSERT INTO recipe_ingredient (id_recipe, id_ingredient, quantity) VALUES (v_recipe, 1, 10);
    INSERT INTO review (id_user, id_recipe, rating, comment) VALUES (1, v_recipe, 5, 'x');
    DELETE FROM recipe WHERE id_recipe = v_recipe;
    ASSERT (SELECT count(*) FROM step WHERE id_recipe = v_recipe) = 0, 'étapes supprimées en cascade';
    ASSERT (SELECT count(*) FROM recipe_ingredient WHERE id_recipe = v_recipe) = 0, 'ingrédients supprimés en cascade';
    ASSERT (SELECT count(*) FROM review WHERE id_recipe = v_recipe) = 0, 'avis supprimés en cascade';
    ASSERT (SELECT count(*) FROM recipe_stock WHERE id_recipe = v_recipe) = 0, 'stock supprimé en cascade';
    RAISE NOTICE 'OK  création et suppression de recette';

    -- 12. Une recette vendue ne peut pas être supprimée (RESTRICT)
    BEGIN
        DELETE FROM recipe WHERE id_recipe = 8;
        RAISE EXCEPTION 'la recette 8 (vendue) aurait dû être protégée';
    EXCEPTION WHEN foreign_key_violation THEN NULL;
    END;
    RAISE NOTICE 'OK  recette vendue protégée';
END $$;

ROLLBACK;
