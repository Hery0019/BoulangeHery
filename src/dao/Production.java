package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Ordre de fabrication : produire {@code quantity} unités d'une recette
 * consomme ses ingrédients et alimente le stock de produit fini, par le
 * trigger {@code trg_apply_production}.
 *
 * Une production est un événement constaté — de la farine a réellement été
 * sortie : elle ne se modifie ni ne se supprime, seule la création existe.
 */
public class Production {

    private int id;
    private int idRecipe;
    private String recipeTitle = "";
    private int idUser;
    private String userName = "";
    private int quantity;
    private LocalDate productionDate = LocalDate.now();

    private static final DateTimeFormatter HUMAN_DATE =
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);

    /** Production et libellés associés, pour l'affichage de la liste. */
    private static final String SELECT_JOINED =
            "SELECT p.*, r.title, u.firstname, u.lastname FROM production p"
                    + " JOIN recipe r ON r.id_recipe = p.id_recipe"
                    + " LEFT JOIN gotta_taste_user u ON u.id_user = p.id_user";

    public Production() {
    }

    public Production(int idRecipe, int idUser, int quantity, LocalDate productionDate) {
        this.idRecipe = idRecipe;
        this.idUser = idUser;
        this.quantity = quantity;
        this.productionDate = productionDate;
    }

    /**
     * Productions filtrées ; comme ailleurs dans le projet, un critère vaut
     * « non renseigné » à 0 (identifiant) ou {@code null} (date).
     */
    public static ArrayList<Production> search(int idRecipe, LocalDate minDate, LocalDate maxDate) throws Exception {
        ArrayList<Production> productions = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_JOINED + " WHERE 1 = 1");
        if (idRecipe != 0) {
            sql.append(" AND p.id_recipe = ?");
        }
        if (minDate != null) {
            sql.append(" AND p.production_date >= ?");
        }
        if (maxDate != null) {
            sql.append(" AND p.production_date <= ?");
        }
        sql.append(" ORDER BY p.production_date DESC, p.id_production DESC");

        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int index = 1;
            if (idRecipe != 0) {
                statement.setInt(index++, idRecipe);
            }
            if (minDate != null) {
                statement.setDate(index++, Date.valueOf(minDate));
            }
            if (maxDate != null) {
                statement.setDate(index++, Date.valueOf(maxDate));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    productions.add(fromRow(resultSet));
                }
            }
        }
        return productions;
    }

    public static ArrayList<Production> all() throws Exception {
        return search(0, null, null);
    }

    /** Enregistre l'ordre ; le trigger refuse si la matière première manque. */
    public void create() throws Exception {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO production (id_recipe, id_user, quantity, production_date)"
                                + " VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, idRecipe);
            if (idUser == 0) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, idUser);
            }
            statement.setInt(3, quantity);
            statement.setDate(4, Date.valueOf(productionDate));
            statement.executeUpdate();
        }
    }

    private static Production fromRow(ResultSet resultSet) throws Exception {
        Production production = new Production();
        production.id = resultSet.getInt("id_production");
        production.idRecipe = resultSet.getInt("id_recipe");
        production.recipeTitle = resultSet.getString("title");
        production.idUser = resultSet.getInt("id_user");
        String firstname = resultSet.getString("firstname");
        production.userName = firstname == null ? "" : firstname + " " + resultSet.getString("lastname");
        production.quantity = resultSet.getInt("quantity");
        production.productionDate = resultSet.getDate("production_date").toLocalDate();
        return production;
    }

    public void setIdRecipe(int idRecipe) {
        this.idRecipe = idRecipe;
    }

    public int getId() {
        return id;
    }

    public int getIdRecipe() {
        return idRecipe;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public int getIdUser() {
        return idUser;
    }

    /** Auteur de l'ordre, vide si le compte a été supprimé. */
    public String getUserName() {
        return userName;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public String getFormattedProductionDate() {
        return productionDate.toString();
    }

    public String getHumanFormattedProductionDate() {
        return productionDate.format(HUMAN_DATE);
    }
}
