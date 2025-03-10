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
