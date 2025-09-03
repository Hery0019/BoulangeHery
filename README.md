# BoulangeHery

Application web de gestion d'une boulangerie : recettes (ingrédients, étapes,
prix), avis, ventes, stock, commissions des vendeurs et historique des prix.

Stack : Java 21, Jakarta Servlet 6 / JSP sur Apache Tomcat 10.1, PostgreSQL ≥ 14
(`CREATE OR REPLACE TRIGGER`), JDBC brut, gabarit Bootstrap « Sneat ».

## Configuration

L'application ne contient aucun identifiant. La base est décrite par trois
valeurs, lues comme propriétés système de la JVM (`-D…`, prioritaires) ou
comme variables d'environnement :

| Propriété système | Variable d'environnement | Exemple |
|---|---|---|
| `boulangerie.db.url` | `BOULANGERIE_DB_URL` | `jdbc:postgresql://localhost:5432/gotta_taste` |
| `boulangerie.db.user` | `BOULANGERIE_DB_USER` | `boulangerie_app` (rôle dédié, pas `postgres`) |
| `boulangerie.db.password` | `BOULANGERIE_DB_PASSWORD` | — |

**Sous Tomcat**, les connexions viennent du pool JNDI `jdbc/boulangerie`
déclaré dans `web/META-INF/context.xml` ; ses valeurs `${boulangerie.db.*}`
sont substituées par Tomcat depuis les propriétés système. Les définir dans
`%CATALINA_HOME%\bin\setenv.bat` (fichier à ne pas versionner) :

```bat
set "CATALINA_OPTS=-Dboulangerie.db.url=jdbc:postgresql://localhost:5432/gotta_taste -Dboulangerie.db.user=boulangerie_app -Dboulangerie.db.password=..."
```

**Hors Tomcat** (outils `tools.UserAdmin`, tests), `DBConnection` se rabat sur
`DriverManager` avec les mêmes propriétés ou les variables d'environnement.

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

(`temp\WEB-INF\classes` est produit par `deploy.bat` ; la configuration
ci-dessus doit être dans l'environnement.)

Les données de démonstration (`3-data.sql`) créent trois comptes dont les mots
de passe sont indiqués en commentaire dans le script : à ne pas utiliser en
production.

## Vérification des règles en base

Après installation (ou migration), rejouer le scénario `sql/tests/triggers_smoke_test.sql`
sur une base chargée avec les données de démonstration : il vérifie en transaction
(annulée à la fin) le stock, les ventes, les commissions, l'historique des prix, le
temps de préparation, l'unicité et les suppressions en cascade.

```
psql -U postgres -d gotta_taste -v ON_ERROR_STOP=1 -f sql/tests/triggers_smoke_test.sql
```

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
- `web/` — JSP et ressources statiques ; `web/META-INF/context.xml` — pool JDBC ; `web.xml` — mappings, filtres, pages d'erreur
- `sql/` — schéma, triggers, données de démo, migrations
- `aleas/` — journal des changements de spécification
