package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Ingredient {

    private int id;
    private String name = "";
    private String unit = "";
    private BigDecimal price = BigDecimal.ZERO; // prix unitaire, NUMERIC(10,2) en base

    public Ingredient() {
    }

    public Ingredient(int id) {
        this.id = id;
    }

    public Ingredient(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }

    public Ingredient(int id, String name, String unit, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.price = price;
    }

    public static ArrayList<Ingredient> all() throws Exception {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM ingredient ORDER BY id_ingredient");
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ingredients.add(fromRow(resultSet));
            }
        }
        return ingredients;
    }

    /** Recherche ; {@code minPrice} / {@code maxPrice} à {@code null} = pas de borne. */
    public static ArrayList<Ingredient> search(String searchName, String searchUnit, BigDecimal minPrice,
            BigDecimal maxPrice) throws Exception {
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM ingredient WHERE ingredient_name ILIKE ? AND unit ILIKE ?");
        if (minPrice != null) {
            sql.append(" AND price >= ?");
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
        }
        sql.append(" ORDER BY id_ingredient");

        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            statement.setString(paramIndex++, "%" + searchName + "%");
            statement.setString(paramIndex++, "%" + searchUnit + "%");
            if (minPrice != null) {
                statement.setBigDecimal(paramIndex++, minPrice);
            }
            if (maxPrice != null) {
                statement.setBigDecimal(paramIndex++, maxPrice);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ingredients.add(fromRow(resultSet));
                }
            }
        }
        return ingredients;
    }

    public void find() throws Exception {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM ingredient WHERE id_ingredient = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    name = resultSet.getString("ingredient_name");
                    unit = resultSet.getString("unit");
                    price = resultSet.getBigDecimal("price");
                }
            }
        }
    }

    public void create() throws Exception {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO ingredient(ingredient_name, unit, price) VALUES (?, ?, ?)")) {
            statement.setString(1, name);
            statement.setString(2, unit);
            statement.setBigDecimal(3, price);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw DuplicateEntityException.orSame(e, "Un ingrédient porte déjà ce nom avec cette unité.");
        }
    }

    public void update() throws Exception {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE ingredient SET ingredient_name = ?, unit = ?, price = ? WHERE id_ingredient = ?")) {
            statement.setString(1, name);
            statement.setString(2, unit);
            statement.setBigDecimal(3, price);
            statement.setInt(4, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw DuplicateEntityException.orSame(e, "Un ingrédient porte déjà ce nom avec cette unité.");
        }
    }

    public void delete() throws Exception {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM ingredient WHERE id_ingredient = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            if (EntityInUseException.isForeignKeyViolation(e)) {
                throw new EntityInUseException(
                        "Cet ingrédient est encore associé à une ou plusieurs recette(s) : il ne peut pas être supprimé.");
            }
            throw e;
        }
    }

    private static Ingredient fromRow(ResultSet resultSet) throws SQLException {
        return new Ingredient(
                resultSet.getInt("id_ingredient"),
                resultSet.getString("ingredient_name"),
                resultSet.getString("unit"),
                resultSet.getBigDecimal("price"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ingredient [id=" + id + ", name=" + name + ", unit=" + unit + ", price=" + price + "]";
    }

}
