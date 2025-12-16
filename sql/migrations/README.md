# Migrations de schéma

- **Installation neuve** : exécuter `1-schema.sql`, `2-trigger.sql`, puis (données de démo) `3-data.sql`.
  Ces trois fichiers reflètent toujours l'état courant du schéma.
- **Base existante** : exécuter les scripts de ce dossier dans l'ordre numérique,
  une seule fois chacun (ils sont écrits pour être rejouables sans dommage).
  Noter dans la table ci-dessous ce qui a été appliqué en production.

| N° | Objet |
|----|-------|
| 001 | Trigger commission : règle en vigueur à la date de vente |
| 002 | Politiques ON DELETE (CASCADE composants de recette, RESTRICT ailleurs) |
| 003 | Historique des prix daté du jour du changement |
| 004 | Mots de passe hachés (élargissement de la colonne + `tools.UserAdmin rehash`) |
| 005 | Stock unique par recette et créé avec elle, ventes modifiables/supprimables sans fausser stock ni commissions, cook_time à la suppression d'étape |
| 006 | Contraintes d'unicité (email utilisateur/vendeur, nom de catégorie/parfum, ingrédient+unité, numéro d'étape par recette) |
| 007 | `ingredient.price` en NUMERIC(10,2) (l'INT arrondissait les prix unitaires) |
| 008 | Vue `recipe_cost` : coût matière par recette |
| 009 | Index sur les clés étrangères et les colonnes de filtre |
| 010 | Rôles des comptes (ADMIN, BOULANGER, VENDEUR) |
| 011 | Stock de matières premières et ordres de production |
| 012 | Pertes (invendus, casse…) et seuil d'alerte de stock |
| 013 | Auteur d'une recette rattaché au compte utilisateur |
