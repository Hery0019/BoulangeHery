package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Stock de matière première (ingredient_stock) : une ligne par ingrédient,
 * créée à 0 par trigger avec l'ingrédient, consommée par les ordres de
 * production (trigger trg_apply_production) et alimentée par
 * {@link #add(int, BigDecimal)}.
 *
 * Les quantités sont décimales : on approvisionne 12,5 kg de farine, pas 12.
 */
public final class IngredientStock {

    private IngredientStock() {
    }

    /** Approvisionnement : ajoute {@code quantity} (&gt; 0) au stock de l'ingrédient. */
    public static void add(int idIngredient, BigDecimal quantity) throws SQLException {
        if (quantity == null || quantity.signum() <= 0) {
            throw new IllegalArgumentException("La quantité à ajouter doit être supérieure à zéro");
        }
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO ingredient_stock (id_ingredient, reste) VALUES (?, ?)"
                                + " ON CONFLICT (id_ingredient)"
                                + " DO UPDATE SET reste = ingredient_stock.reste + EXCLUDED.reste")) {
            statement.setInt(1, idIngredient);
            statement.setBigDecimal(2, quantity);
            statement.executeUpdate();
        }
    }
}
