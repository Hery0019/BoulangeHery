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
            "SELECT r.*, c.cost FROM recipe r JOIN recipe_cost c ON c.id_recipe = r.id_recipe";

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
                String createdBy = resultSet.getString("created_by");
                LocalDate createdDate = resultSet.getDate("created_date").toLocalDate();
                double price = resultSet.getDouble("price");
                String picture = resultSet.getString("picture");

                Recipe recipe = new Recipe(id, title, description, idCategory, idPerfume, cookTime, createdBy,
                        createdDate, price, picture);
                recipe.setCost(resultSet.getDouble("cost"));
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
                createdBy = resultSet.getString("created_by");
                createdDate = resultSet.getDate("created_date").toLocalDate();
                price = resultSet.getDouble("price");
                picture = resultSet.getString("picture");
                cost = resultSet.getDouble("cost");
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
                recipe.setCreatedBy(resultSet.getString("created_by"));
                recipe.setCreatedDate(resultSet.getDate("created_date").toLocalDate());
                recipe.setPrice(resultSet.getDouble("price"));
                recipe.setPicture(resultSet.getString("picture"));
                recipe.setCost(resultSet.getDouble("cost"));
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
                    "INSERT INTO recipe(title, recipe_description, id_category, id_perfume, cook_time, created_by, created_date, price, picture)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, title);
            statement.setString(2, description);
            statement.setInt(3, idCategory);
            statement.setInt(4, idPerfume);
            statement.setTime(5, Time.valueOf(cookTime));
            statement.setString(6, createdBy);
            statement.setDate(7, Date.valueOf(createdDate));
            statement.setDouble(8, price);
            statement.setString(9, "assets/img/recipies/croissants.jpg");
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
                            + " SET title = ?, recipe_description = ?, id_category = ?, id_perfume = ?, created_by = ?, created_date = ?, price = ? "
                            + " WHERE id_recipe = ?");
            statement.setString(1, title);
            statement.setString(2, description);
            statement.setInt(3, idCategory);
            statement.setInt(4, idPerfume);
            statement.setString(5, createdBy);
            statement.setDate(6, Date.valueOf(createdDate));
            statement.setDouble(7, price);
            statement.setInt(8, id);
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

    public static ArrayList<Recipe> search(
            String searchTitle,
            String searchDescription,
            int searchIdCategory,
            int searchIdPerfume,
            LocalTime minCookTime,
            LocalTime maxCookTime,
            String searchCreator,
            LocalDate minCreationDate,
            LocalDate maxCreationDate,
            String[] idIngredients,
            double minPrice,
            double maxPrice) throws Exception {

        ArrayList<Recipe> recipes = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();

            StringBuilder sql = new StringBuilder(
                    SELECT_WITH_COST +
                            " WHERE title ILIKE ?" +
                            " AND recipe_description ILIKE ?");

            if (searchIdCategory != 0) {
                sql.append(" AND id_category = ?");
            }
            if (searchIdPerfume != 0) {
                sql.append(" AND id_perfume = ?");
            }
            if (minCookTime != null) {
                sql.append(" AND cook_time >= ?");
            }
            if (maxCookTime != null) {
                sql.append(" AND cook_time <= ?");
            }
            sql.append(" AND created_by ILIKE ?");

            if (minCreationDate != null) {
                sql.append(" AND created_date >= ?");
            }
            if (maxCreationDate != null) {
                sql.append(" AND created_date <= ?");
            }

            if (minPrice != 0.0) {
                sql.append(" AND r.price >= ?");
            }
            if (maxPrice != 0.0) {
                sql.append(" AND r.price <= ?");
            }

            if (idIngredients != null && idIngredients.length > 0) {
                sql.append(" AND r.id_recipe IN (");
                sql.append(" SELECT id_recipe FROM recipe_ingredient WHERE id_ingredient IN (");
                sql.append("?");
                for (int i = 1; i < idIngredients.length; i++) {
                    sql.append(", ?");
                }
                sql.append(") GROUP BY id_recipe HAVING COUNT(DISTINCT id_ingredient) = ?)");
            }

            sql.append(" ORDER BY r.id_recipe ASC");

            statement = connection.prepareStatement(sql.toString());

            int paramIndex = 1;
            statement.setString(paramIndex++, "%" + searchTitle.toLowerCase() + "%");
            statement.setString(paramIndex++, "%" + searchDescription + "%");

            if (searchIdCategory != 0) {
                statement.setInt(paramIndex++, searchIdCategory);
            }
            if (searchIdPerfume != 0) {
                statement.setInt(paramIndex++, searchIdPerfume);
            }
            if (minCookTime != null) {
                statement.setTime(paramIndex++, Time.valueOf(minCookTime));
            }
            if (maxCookTime != null) {
                statement.setTime(paramIndex++, Time.valueOf(maxCookTime));
            }
            statement.setString(paramIndex++, "%" + searchCreator + "%");

            if (minCreationDate != null) {
                statement.setDate(paramIndex++, Date.valueOf(minCreationDate));
            }
            if (maxCreationDate != null) {
                statement.setDate(paramIndex++, Date.valueOf(maxCreationDate));
            }

            if (minPrice != 0.0) {
                statement.setDouble(paramIndex++, minPrice);
            }

            if (maxPrice != 0.0) {
                statement.setDouble(paramIndex++, maxPrice);
            }

            if (idIngredients != null && idIngredients.length > 0) {
                for (String idIngredient : idIngredients) {
                    statement.setInt(paramIndex++, Integer.parseInt(idIngredient));
                }
                statement.setInt(paramIndex++, idIngredients.length);
            }
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id_recipe");
                String title = resultSet.getString("title");
                String description = resultSet.getString("recipe_description");
                int idCategory = resultSet.getInt("id_category");
                int idPerfume = resultSet.getInt("id_perfume");
                LocalTime cookTime = resultSet.getTime("cook_time").toLocalTime();
                String createdBy = resultSet.getString("created_by");
                LocalDate createdDate = resultSet.getDate("created_date").toLocalDate();
                double price = resultSet.getDouble("price");
                String picture = resultSet.getString("picture");

                Recipe recipe = new Recipe(id, title, description, idCategory, idPerfume, cookTime, createdBy,
                        createdDate, price, picture);
                recipe.setCost(resultSet.getDouble("cost"));
                recipes.add(recipe);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (resultSet != null)
                resultSet.close();
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        }

        return recipes;
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
