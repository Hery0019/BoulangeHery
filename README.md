# BoulangeHery

Application web de gestion d'une boulangerie : recettes (ingrédients, étapes,
prix), avis, ventes, stock, commissions des vendeurs et historique des prix.

Stack : Java 21, Jakarta Servlet 6 / JSP sur Apache Tomcat 10.1, PostgreSQL ≥ 14
(`CREATE OR REPLACE TRIGGER`), JDBC brut, gabarit Bootstrap « Sneat ».

## Configuration

L'application ne contient aucun identifiant : la base est décrite par
l'environnement de la JVM (Tomcat ou outil en ligne de commande).

| Variable | Exemple |
|---|---|
| `BOULANGERIE_DB_URL` | `jdbc:postgresql://localhost:5432/gotta_taste` |
| `BOULANGERIE_DB_USER` | `boulangerie_app` (rôle dédié, pas `postgres`) |
| `BOULANGERIE_DB_PASSWORD` | — |

Les propriétés système `boulangerie.db.url|user|password` (`-D…`) ont priorité.
Sous Tomcat, le plus simple est un fichier `%CATALINA_HOME%\bin\setenv.bat` :

```bat
set "BOULANGERIE_DB_URL=jdbc:postgresql://localhost:5432/gotta_taste"
set "BOULANGERIE_DB_USER=boulangerie_app"
set "BOULANGERIE_DB_PASSWORD=..."
```

Ce fichier ne doit pas être versionné.

## Base de données

Installation neuve, dans l'ordre :

```
psql -U postgres -f sql/1-schema.sql      # crée la base gotta_taste et les tables
psql -U postgres -d gotta_taste -f sql/2-trigger.sql
psql -U postgres -d gotta_taste -f sql/3-data.sql   # données de démonstration (facultatif)
```

Base existante : appliquer les scripts de `sql/migrations/` dans l'ordre (voir
le README de ce dossier).

Rôle applicatif recommandé :

```sql
CREATE ROLE boulangerie_app LOGIN PASSWORD '...';
GRANT CONNECT ON DATABASE gotta_taste TO boulangerie_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO boulangerie_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO boulangerie_app;
```

## Comptes utilisateurs

Les mots de passe sont stockés hachés (PBKDF2-HMAC-SHA256). Pour créer un
compte ou convertir une base ancienne :

```
java -cp "temp\WEB-INF\classes;lib\*" tools.UserAdmin create <prénom> <nom> <email> <mot de passe>
java -cp "temp\WEB-INF\classes;lib\*" tools.UserAdmin rehash
```

(`temp\WEB-INF\classes` est produit par `deploy.bat` ; les variables
d'environnement ci-dessus doivent être définies.)

Les données de démonstration (`3-data.sql`) créent trois comptes dont les mots
de passe sont indiqués en commentaire dans le script : à ne pas utiliser en
production.

## Build et déploiement

```
set CATALINA_HOME=C:\chemin\vers\tomcat
deploy.bat      # compile, construit boulangerie.war et le copie dans webapps
run.bat         # démarre Tomcat et ouvre http://localhost:8080/boulangerie/
```

`deploy.bat` accepte aussi `WEB_APPS` pour cibler un autre dossier `webapps`.

## Organisation du code

- `src/dao` — entités et accès JDBC (une classe par table)
- `src/servlet` — points d'entrée HTTP (`XxxServlet` = liste/mutation, `FormXxxServlet` = formulaire), filtres `AuthFilter` et `ErrorFilter`
- `src/util` — paramètres de requête, session, échappement HTML, hachage
- `src/tools` — outils en ligne de commande
- `web/` — JSP et ressources statiques ; `web.xml` — mappings, filtres, pages d'erreur
- `sql/` — schéma, triggers, données de démo, migrations
- `aleas/` — journal des changements de spécification
