package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Agrégations du tableau de bord.
 *
 * Tout est calculé par la base en une requête par indicateur : les données
 * existent déjà (ventes, commissions, stock, pertes, coût matière), elles
 * n'étaient simplement jamais rapprochées.
 */
public final class Dashboard {

    /** Un point de série : un libellé et sa valeur. */
    public record Point(String label, double value) {
    }

    private Dashboard() {
    }

    /** Chiffre d'affaires encaissé par jour, sur les {@code days} derniers jours. */
    public static List<Point> revenueByDay(int days) throws Exception {
        return points(
                "SELECT to_char(d.day, 'DD/MM') AS label, COALESCE(SUM(s.argent - s.reste), 0) AS value"
                        + " FROM generate_series(CURRENT_DATE - ?::int, CURRENT_DATE, '1 day') AS d(day)"
                        + " LEFT JOIN recipe_sell s ON s.sell_date = d.day"
                        + " GROUP BY d.day ORDER BY d.day",
                days - 1);
    }

    /** Quantités vendues par recette, les plus vendues d'abord. */
    public static List<Point> topRecipes(int limit) throws Exception {
        return points(
                "SELECT r.title AS label, SUM(s.combien) AS value"
                        + " FROM recipe_sell s JOIN recipe r ON r.id_recipe = s.id_recipe"
                        + " GROUP BY r.title ORDER BY value DESC LIMIT ?",
                limit);
    }

    /** Chiffre d'affaires par catégorie. */
    public static List<Point> revenueByCategory() throws Exception {
        return points(
                "SELECT c.category_name AS label, SUM(s.argent - s.reste) AS value"
                        + " FROM recipe_sell s JOIN category c ON c.id_category = s.id_category"
                        + " GROUP BY c.category_name ORDER BY value DESC");
    }

    /** Commissions cumulées par vendeur. */
    public static List<Point> commissionsBySeller() throws Exception {
        return points(
                "SELECT v.firstname || ' ' || v.lastname AS label, SUM(c.commission_amount) AS value"
                        + " FROM commission c JOIN vendeur v ON v.id_vendeur = c.id_vendeur"
                        + " GROUP BY label ORDER BY value DESC");
    }

    /** Quantités perdues par motif sur les {@code days} derniers jours. */
    public static List<Point> lossesByReason(int days) throws Exception {
        return points(
                "SELECT reason AS label, SUM(quantity) AS value FROM recipe_loss"
                        + " WHERE loss_date >= CURRENT_DATE - ?::int"
                        + " GROUP BY reason ORDER BY value DESC",
                days);
    }

    /** Unités produites par jour, sur les {@code days} derniers jours. */
    public static List<Point> productionByDay(int days) throws Exception {
        return points(
                "SELECT to_char(d.day, 'DD/MM') AS label, COALESCE(SUM(p.quantity), 0) AS value"
                        + " FROM generate_series(CURRENT_DATE - ?::int, CURRENT_DATE, '1 day') AS d(day)"
                        + " LEFT JOIN production p ON p.production_date = d.day"
                        + " GROUP BY d.day ORDER BY d.day",
                days - 1);
    }

    /** Marge unitaire par recette, la plus faible d'abord : ce qu'il faut surveiller. */
    public static List<Point> thinnestMargins(int limit) throws Exception {
        return points(
                "SELECT r.title AS label, r.price - c.cost AS value"
                        + " FROM recipe r JOIN recipe_cost c ON c.id_recipe = r.id_recipe"
                        + " WHERE c.cost > 0 ORDER BY value ASC LIMIT ?",
                limit);
    }

    /** Recettes retombées à leur seuil d'alerte : libellé « reste / seuil ». */
    public static List<Point> lowStock() throws Exception {
        return points(
                "SELECT r.title || ' (' || s.reste || ' / ' || s.seuil_alerte || ')' AS label, s.reste AS value"
                        + " FROM recipe_stock s JOIN recipe r ON r.id_recipe = s.id_recipe"
                        + " WHERE s.seuil_alerte > 0 AND s.reste <= s.seuil_alerte"
                        + " ORDER BY s.reste ASC");
    }

    /** Matières premières épuisées ou proches de l'être. */
    public static List<Point> lowIngredients(int limit) throws Exception {
        return points(
                "SELECT i.ingredient_name || ' (' || s.reste || ' ' || i.unit || ')' AS label, s.reste AS value"
                        + " FROM ingredient_stock s JOIN ingredient i ON i.id_ingredient = s.id_ingredient"
                        + " ORDER BY s.reste ASC LIMIT ?",
                limit);
    }

    /** Somme simple servant d'indicateur ; renvoie 0 quand il n'y a rien. */
    public static double scalar(String sql) throws Exception {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getDouble(1) : 0.0;
        }
    }

    private static List<Point> points(String sql, int... params) throws Exception {
        List<Point> points = new ArrayList<>();
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setInt(i + 1, params[i]);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    points.add(new Point(resultSet.getString("label"), resultSet.getDouble("value")));
                }
            }
        }
        return points;
    }
}
