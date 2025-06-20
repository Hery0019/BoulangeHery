package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Stock d'une recette (recipe_stock) : une ligne par recette, créée à 0 par
 * trigger à la création de la recette, décrémentée par les ventes (trigger
 * trg_update_recipe_stock) et alimentée par {@link #add(int, int)}.
 */
public class RecipeStock {

    private final int id;
    private final int idRecipe;
    private final int reste;

    public RecipeStock(int id, int idRecipe, int reste) {
        this.id = id;
        this.idRecipe = idRecipe;
        this.reste = reste;
    }

    /** Stock de la recette, ou {@code null} si aucune ligne n'existe (base antérieure à la migration 005). */
    public static RecipeStock findByRecipe(int idRecipe) throws SQLException {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT id_recipe_stock, id_recipe, reste FROM recipe_stock WHERE id_recipe = ?")) {
            statement.setInt(1, idRecipe);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new RecipeStock(
                            resultSet.getInt("id_recipe_stock"),
                            resultSet.getInt("id_recipe"),
                            resultSet.getInt("reste"));
                }
                return null;
            }
        }
    }

    /** Approvisionnement : ajoute {@code quantity} (&gt; 0) au stock, en créant la ligne si besoin. */
    public static void add(int idRecipe, int quantity) throws SQLException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité à ajouter doit être supérieure à zéro");
        }
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO recipe_stock (id_recipe, reste) VALUES (?, ?)"
                                + " ON CONFLICT (id_recipe) DO UPDATE SET reste = recipe_stock.reste + EXCLUDED.reste")) {
            statement.setInt(1, idRecipe);
            statement.setInt(2, quantity);
            statement.executeUpdate();
        }
    }

    public int getId() {
        return id;
    }

    public int getIdRecipe() {
        return idRecipe;
    }

    public int getReste() {
        return reste;
    }

}
