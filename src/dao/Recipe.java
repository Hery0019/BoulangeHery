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
            statement = connection.prepareStatement("SELECT * FROM recipe");
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

                recipes.add(
                        new Recipe(id, title, description, idCategory, idPerfume, cookTime, createdBy, createdDate, price, picture));
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
                    "SELECT * FROM recipe WHERE id_recipe = ?");
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
                    "SELECT * FROM recipe WHERE id_recipe = ?");
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
            connection.rollback();
            throw e;
        } finally {
            statement.close();
            connection.close();
        }
    }

    public void update() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBConnection.getPostgesConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(
                    "UPDATE recipe"
                            + " SET title = ?, recipe_description = ?, id_category = ?, id_perfume = ?, cook_time = ?, created_by = ?, created_date = ?, price = ? "
                            + " WHERE id_recipe = ?");
            statement.setString(1, title);
            statement.setString(2, description);
            statement.setInt(3, idCategory);
            statement.setInt(4, idPerfume);
            statement.setTime(5, Time.valueOf(cookTime));
            statement.setString(6, createdBy);
            statement.setDate(7, Date.valueOf(createdDate));
            statement.setDouble(8, price);
            statement.setInt(9, id);
            statement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            statement.close();
            connection.close();
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
                    "SELECT DISTINCT r.* " +
                            "FROM recipe r " +
                            "LEFT JOIN recipe_ingredient ri ON r.id_recipe = ri.id_recipe " +
                            "LEFT JOIN ingredient i ON ri.id_ingredient = i.id_ingredient " +
                            "WHERE title ILIKE ? " +
                            "AND recipe_description ILIKE ?");

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

            if (minPrice >= 0) {
                statement.setDouble(paramIndex++, minPrice);
            }

            if (maxPrice >= 0) {
                statement.setDouble(paramIndex++, maxPrice);
            }

            if (idIngredients != null && idIngredients.length > 0) {
                for (String idIngredient : idIngredients) {
                    statement.setInt(paramIndex++, Integer.parseInt(idIngredient));
                }
                statement.setInt(paramIndex++, idIngredients.length);
            }
            System.out.println("Query: " + sql.toString());
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

                recipes.add(new Recipe(id, title, description, idCategory, idPerfume, cookTime, createdBy, createdDate,
                        price, picture));
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
            // Delete all the recipe's ingredient before deleting
            // the recipe itself
            RecipeIngredient recipeIngredient = new RecipeIngredient(id);
            recipeIngredient.deleteFromIdRecipe();

            // Delete all the recipe's review
            Review review = new Review();
            review.setIdRecipe(id);
            review.deleteFromIdRecipe();

            connection = DBConnection.getPostgesConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(
                    "DELETE FROM recipe"
                            + " WHERE id_recipe = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            statement.close();
            connection.close();
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
