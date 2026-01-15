package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Locale;

public class Recipe {

    private int id;
    private String title = "";
    private String description = "";
    private int idCategory = 1;
    private int idPerfume = 1; // Nouvel attribut
    private LocalTime cookTime = LocalTime.of(0, 0, 0);
    private String createdBy = "";
    private LocalDate createdDate = LocalDate.now();
    private double price = 0.0;
    private String picture = "";
    /** Coût matière, lu dans la vue recipe_cost (jamais saisi). */
    private double cost = 0.0;
    /** Stock de produit fini et seuil d'alerte, lus dans recipe_stock. */
    private int stock = 0;
    private int seuilAlerte = 0;
    /** Compte auteur ; 0 tant que la recette n'est pas rattachée. */
    private int idCreatedBy = 0;

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter humanTimeFormatter = new DateTimeFormatterBuilder()
            .appendPattern("H")
            .appendLiteral(" heure ")
            .optionalStart()
            .appendPattern("m")
            .appendLiteral(" minute")
            .optionalEnd()
            .toFormatter(Locale.FRENCH);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter humanDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy",
            Locale.FRENCH);

    /** Recette et son coût matière : la vue fournit une ligne par recette. */
    private static final String SELECT_WITH_COST =
            "SELECT r.*, c.cost, COALESCE(s.reste, 0) AS stock, COALESCE(s.seuil_alerte, 0) AS seuil_alerte,"
                    // Auteur : le compte rattaché, ou l'ancien texte libre des recettes non rattachées
                    + " COALESCE(u.firstname || ' ' || u.lastname, r.created_by, '') AS author"
                    + " FROM recipe r"
                    + " JOIN recipe_cost c ON c.id_recipe = r.id_recipe"
                    + " LEFT JOIN recipe_stock s ON s.id_recipe = r.id_recipe"
                    + " LEFT JOIN gotta_taste_user u ON u.id_user = r.id_created_by";

    public Recipe() {
    }

    public Recipe(int id) {
        this.id = id;
    }

    public Recipe(String title, String description, int idCategory, int idPerfume, LocalTime cookTime, String createdBy,
            LocalDate createdDate) {
        this.title = title;
        this.description = description;
        this.idCategory = idCategory;
        this.idPerfume = idPerfume;
        this.cookTime = cookTime;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    public Recipe(int id, String title, String description, int idCategory, int idPerfume, LocalTime cookTime,
            String createdBy, LocalDate createdDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.idCategory = idCategory;
        this.idPerfume = idPerfume;
        this.cookTime = cookTime;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    public Recipe(int id, String title, String description, int idCategory, int idPerfume, LocalTime cookTime,
            String createdBy, LocalDate createdDate, double price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.idCategory = idCategory;
        this.idPerfume = idPerfume;
        this.cookTime = cookTime;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.price = price;
    }
    
    public Recipe(int id, String title, String description, int idCategory, int idPerfume, LocalTime cookTime,
            String createdBy, LocalDate createdDate, double price, String picture) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.idCategory = idCategory;
        this.idPerfume = idPerfume;
        this.cookTime = cookTime;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.price = price;
        this.picture = picture;
    }

    public static ArrayList<Recipe> all() throws Exception {
        ArrayList<Recipe> recipes = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();
            statement = connection.prepareStatement(SELECT_WITH_COST);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id_recipe");
                String title = resultSet.getString("title");
                String description = resultSet.getString("recipe_description");
                int idCategory = resultSet.getInt("id_category");
                int idPerfume = resultSet.getInt("id_perfume");
                LocalTime cookTime = resultSet.getTime("cook_time").toLocalTime();
                String createdBy = resultSet.getString("author");
                LocalDate createdDate = resultSet.getDate("created_date").toLocalDate();
                double price = resultSet.getDouble("price");
                String picture = resultSet.getString("picture");

                Recipe recipe = new Recipe(id, title, description, idCategory, idPerfume, cookTime, createdBy,
                        createdDate, price, picture);
                recipe.setCost(resultSet.getDouble("cost"));
                recipe.setStock(resultSet.getInt("stock"), resultSet.getInt("seuil_alerte"));
                recipe.setIdCreatedBy(resultSet.getInt("id_created_by"));
                recipes.add(recipe);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return recipes;
    }

    public void find() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();
            statement = connection.prepareStatement(
                    SELECT_WITH_COST + " WHERE r.id_recipe = ?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                id = resultSet.getInt("id_recipe");
                title = resultSet.getString("title");
                description = resultSet.getString("recipe_description");
                idCategory = resultSet.getInt("id_category");
                idPerfume = resultSet.getInt("id_perfume");
                cookTime = resultSet.getTime("cook_time").toLocalTime();
                createdBy = resultSet.getString("author");
                createdDate = resultSet.getDate("created_date").toLocalDate();
                price = resultSet.getDouble("price");
                picture = resultSet.getString("picture");
                cost = resultSet.getDouble("cost");
                stock = resultSet.getInt("stock");
                seuilAlerte = resultSet.getInt("seuil_alerte");
                idCreatedBy = resultSet.getInt("id_created_by");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static Recipe findById(int id) throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
    
        try {
            connection = DBConnection.getPostgesConnection();
            statement = connection.prepareStatement(
                    SELECT_WITH_COST + " WHERE r.id_recipe = ?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(resultSet.getInt("id_recipe"));
                recipe.setTitle(resultSet.getString("title"));
                recipe.setDescription(resultSet.getString("recipe_description"));
                recipe.setIdCategory(resultSet.getInt("id_category"));
                recipe.setIdPerfume(resultSet.getInt("id_perfume"));
                recipe.setCookTime(resultSet.getTime("cook_time").toLocalTime());
                recipe.setCreatedBy(resultSet.getString("author"));
                recipe.setCreatedDate(resultSet.getDate("created_date").toLocalDate());
                recipe.setPrice(resultSet.getDouble("price"));
                recipe.setPicture(resultSet.getString("picture"));
                recipe.setCost(resultSet.getDouble("cost"));
                recipe.setStock(resultSet.getInt("stock"), resultSet.getInt("seuil_alerte"));
                recipe.setIdCreatedBy(resultSet.getInt("id_created_by"));
                return recipe;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    
        return null; // Retourne null si aucune recette n'a été trouvée
    }
    
    

    public void create() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBConnection.getPostgesConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(
                    "INSERT INTO recipe(title, recipe_description, id_category, id_perfume, cook_time, id_created_by, created_date, price, picture)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, title);
            statement.setString(2, description);
            statement.setInt(3, idCategory);
            statement.setInt(4, idPerfume);
            statement.setTime(5, Time.valueOf(cookTime));
            statement.setInt(6, idCreatedBy);
            statement.setDate(7, Date.valueOf(createdDate));
            statement.setDouble(8, price);
            statement.setString(9, picture); // null si aucune photo n'a été envoyée
            statement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            if (connection != null) connection.rollback();
            throw e;
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }

    public void update() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBConnection.getPostgesConnection();
            connection.setAutoCommit(false);
            // cook_time n'est pas modifiable ici : il est recalculé par trigger depuis les étapes.
            statement = connection.prepareStatement(
                    "UPDATE recipe"
                            + " SET title = ?, recipe_description = ?, id_category = ?, id_perfume = ?,"
                            + " id_created_by = ?, created_by = NULL, created_date = ?, price = ?,"
                            // photo laissée telle quelle quand le formulaire n'en envoie pas de nouvelle
                            + " picture = COALESCE(NULLIF(?, ''), picture)"
                            + " WHERE id_recipe = ?");
            statement.setString(1, title);
            statement.setString(2, description);
            statement.setInt(3, idCategory);
            statement.setInt(4, idPerfume);
            statement.setInt(5, idCreatedBy);
            statement.setDate(6, Date.valueOf(createdDate));
            statement.setDouble(7, price);
            statement.setString(8, picture);
            statement.setInt(9, id);
            statement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            if (connection != null) connection.rollback();
            throw e;
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }

    /** Tris proposés, associés à leur clause SQL : rien d'autre n'est accepté. */
    private static final java.util.Map<String, String> SORTS = java.util.Map.of(
            "recent", "r.created_date DESC, r.id_recipe DESC",
            "title", "r.title ASC",
            "price", "r.price DESC",
            "margin", "(r.price - c.cost) DESC",
            "stock", "COALESCE(s.reste, 0) ASC",
            "id", "r.id_recipe ASC");

    public static final String DEFAULT_SORT = "id";
    public static final int PAGE_SIZE = 6; // deux rangées de trois cartes

    public static boolean isKnownSort(String sort) {
        return SORTS.containsKey(sort);
    }

    /**
     * Critères communs à {@link #search} et {@link #countSearch}, écrits une
     * seule fois : la liste et son total ne peuvent pas diverger.
     *
     * Convention du projet : 0 (identifiants, prix), {@code null} (dates,
     * heures) et {@code ""} (texte) valent « critère non renseigné ».
     */
    private static void criteria(StringBuilder sql, java.util.List<Object> params,
            String searchTitle, String searchDescription, int searchIdCategory, int searchIdPerfume,
            LocalTime minCookTime, LocalTime maxCookTime, String searchCreator,
            LocalDate minCreationDate, LocalDate maxCreationDate, String[] idIngredients,
            double minPrice, double maxPrice) {

        sql.append(" WHERE title ILIKE ? AND recipe_description ILIKE ?");
        params.add("%" + searchTitle.toLowerCase() + "%");
        params.add("%" + searchDescription + "%");

        if (searchIdCategory != 0) {
            sql.append(" AND id_category = ?");
            params.add(searchIdCategory);
        }
        if (searchIdPerfume != 0) {
            sql.append(" AND id_perfume = ?");
            params.add(searchIdPerfume);
        }
        if (minCookTime != null) {
            sql.append(" AND cook_time >= ?");
            params.add(Time.valueOf(minCookTime));
        }
        if (maxCookTime != null) {
            sql.append(" AND cook_time <= ?");
            params.add(Time.valueOf(maxCookTime));
        }
        sql.append(" AND COALESCE(u.firstname || ' ' || u.lastname, r.created_by, '') ILIKE ?");
        params.add("%" + searchCreator + "%");

        if (minCreationDate != null) {
            sql.append(" AND created_date >= ?");
            params.add(Date.valueOf(minCreationDate));
        }
        if (maxCreationDate != null) {
            sql.append(" AND created_date <= ?");
            params.add(Date.valueOf(maxCreationDate));
        }
        if (minPrice != 0.0) {
            sql.append(" AND r.price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != 0.0) {
            sql.append(" AND r.price <= ?");
            params.add(maxPrice);
        }
        if (idIngredients != null && idIngredients.length > 0) {
            sql.append(" AND r.id_recipe IN (SELECT id_recipe FROM recipe_ingredient WHERE id_ingredient IN (?");
            for (int i = 1; i < idIngredients.length; i++) {
                sql.append(", ?");
            }
            sql.append(") GROUP BY id_recipe HAVING COUNT(DISTINCT id_ingredient) = ?)");
            for (String idIngredient : idIngredients) {
                params.add(Integer.parseInt(idIngredient));
            }
            params.add(idIngredients.length);
        }
    }

    /** Nombre de recettes correspondant aux critères, pour la pagination. */
    public static int countSearch(
            String searchTitle, String searchDescription, int searchIdCategory, int searchIdPerfume,
            LocalTime minCookTime, LocalTime maxCookTime, String searchCreator,
            LocalDate minCreationDate, LocalDate maxCreationDate, String[] idIngredients,
            double minPrice, double maxPrice) throws Exception {

        StringBuilder sql = new StringBuilder(
                "SELECT count(*) FROM recipe r"
                        + " JOIN recipe_cost c ON c.id_recipe = r.id_recipe"
                        + " LEFT JOIN recipe_stock s ON s.id_recipe = r.id_recipe"
                        + " LEFT JOIN gotta_taste_user u ON u.id_user = r.id_created_by");
        java.util.List<Object> params = new java.util.ArrayList<>();
        criteria(sql, params, searchTitle, searchDescription, searchIdCategory, searchIdPerfume, minCookTime,
                maxCookTime, searchCreator, minCreationDate, maxCreationDate, idIngredients, minPrice, maxPrice);

        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            bind(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        }
    }

    /**
     * Une page de résultats. {@code sort} doit être une clé de {@link #SORTS}
     * (sinon le tri par défaut s'applique) et {@code page} commence à 1.
     */
    public static ArrayList<Recipe> search(
            String searchTitle, String searchDescription, int searchIdCategory, int searchIdPerfume,
            LocalTime minCookTime, LocalTime maxCookTime, String searchCreator,
            LocalDate minCreationDate, LocalDate maxCreationDate, String[] idIngredients,
            double minPrice, double maxPrice, String sort, int page, int pageSize) throws Exception {

        ArrayList<Recipe> recipes = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_WITH_COST);
        java.util.List<Object> params = new java.util.ArrayList<>();
        criteria(sql, params, searchTitle, searchDescription, searchIdCategory, searchIdPerfume, minCookTime,
                maxCookTime, searchCreator, minCreationDate, maxCreationDate, idIngredients, minPrice, maxPrice);

        sql.append(" ORDER BY ").append(SORTS.getOrDefault(sort, SORTS.get(DEFAULT_SORT)));
        if (pageSize > 0) {
            sql.append(" LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add((Math.max(page, 1) - 1) * pageSize);
        }

        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            bind(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Recipe recipe = new Recipe(
                            resultSet.getInt("id_recipe"),
                            resultSet.getString("title"),
                            resultSet.getString("recipe_description"),
                            resultSet.getInt("id_category"),
                            resultSet.getInt("id_perfume"),
                            resultSet.getTime("cook_time").toLocalTime(),
                            resultSet.getString("author"),
                            resultSet.getDate("created_date").toLocalDate(),
                            resultSet.getDouble("price"),
                            resultSet.getString("picture"));
                    recipe.setCost(resultSet.getDouble("cost"));
                    recipe.setStock(resultSet.getInt("stock"), resultSet.getInt("seuil_alerte"));
                    recipe.setIdCreatedBy(resultSet.getInt("id_created_by"));
                    recipes.add(recipe);
                }
            }
        }
        return recipes;
    }

    private static void bind(PreparedStatement statement, java.util.List<Object> params) throws Exception {
        for (int i = 0; i < params.size(); i++) {
            statement.setObject(i + 1, params.get(i));
        }
    }

    public void delete() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBConnection.getPostgesConnection();
            connection.setAutoCommit(false);
            // Ingrédients, étapes, avis, stock et historique des prix suivent par ON DELETE CASCADE ; ventes et commissions bloquent (RESTRICT).
            statement = connection.prepareStatement("DELETE FROM recipe WHERE id_recipe = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            if (connection != null) connection.rollback();
            if (EntityInUseException.isForeignKeyViolation(e)) {
                throw new EntityInUseException("Cette recette a des ventes ou des commissions enregistrées : elle ne peut pas être supprimée.");
            }
            throw e;
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public int getIdPerfume() {
        return idPerfume;
    }

    /** Coût matière de la recette (somme quantité x prix unitaire des ingrédients). */
    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getIdCreatedBy() {
        return idCreatedBy;
    }

    public void setIdCreatedBy(int idCreatedBy) {
        this.idCreatedBy = idCreatedBy;
    }

    public int getStock() {
        return stock;
    }

    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setStock(int stock, int seuilAlerte) {
        this.stock = stock;
        this.seuilAlerte = seuilAlerte;
    }

    /** Vrai quand un seuil est défini et que le stock est retombé dessous. */
    public boolean isStockLow() {
        return seuilAlerte > 0 && stock <= seuilAlerte;
    }

    /** Marge brute : prix de vente moins coût matière. Négative si la recette est vendue à perte. */
    public double getMargin() {
        return price - cost;
    }

    /** Taux de marge en pourcentage du prix de vente ; 0 si le prix n'est pas renseigné. */
    public double getMarginRate() {
        return price == 0.0 ? 0.0 : getMargin() / price * 100.0;
    }

    /** Vrai tant qu'aucun ingrédient n'a été associé : la marge affichée n'aurait pas de sens. */
    public boolean hasNoCost() {
        return cost == 0.0;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setIdPerfume(int idPerfume) {
        this.idPerfume = idPerfume;
    }

    public String getDescriptionExcerpt() {
        return description.length() < 21 ? description : description.substring(0, 21) + "...";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public LocalTime getCookTime() {
        return cookTime;
    }

    public String getFormattedCookTime() {
        return cookTime.format(timeFormatter);
    }

    public String getHumanFormattedCookTime() {
        return cookTime.format(humanTimeFormatter);
    }

    public void setCookTime(LocalTime cookTime) {
        this.cookTime = cookTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public String getFormattedCreatedDate() {
        return createdDate.format(dateFormatter);
    }

    public String getHumanFormattedCreatedDate() {
        return createdDate.format(humanDateFormatter);
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Chemin de l'image à afficher : la photo de la recette, ou une image
     * neutre tant qu'aucune n'a été téléversée.
     */
    public String getPictureUrl() {
        return picture == null || picture.isBlank() ? "assets/img/recipe-placeholder.svg" : picture;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    

    @Override
    public String toString() {
        return "Recipe [id=" + id + ", title=" + title + ", description=" + description + ", idCategory=" + idCategory
                + ", cookTime=" + cookTime + ", createdBy=" + createdBy + ", createdDate=" + createdDate + "]";
    }

}
