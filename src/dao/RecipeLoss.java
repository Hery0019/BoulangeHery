package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Sortie de stock constatée hors vente : invendu de fin de journée, casse,
 * péremption ou don. Le trigger {@code trg_apply_recipe_loss} décrémente le
 * stock et refuse ce qui dépasse le disponible.
 *
 * Comme une production, une perte est un constat : elle ne se modifie pas.
 */
public class RecipeLoss {

    /** Motifs acceptés par la contrainte de la base, avec leur libellé. */
    public static final Map<String, String> REASONS = new LinkedHashMap<>();

    static {
        REASONS.put("INVENDU", "Invendu");
        REASONS.put("CASSE", "Casse");
        REASONS.put("PERIME", "Périmé");
        REASONS.put("OFFERT", "Offert");
    }

    private int id;
    private int idRecipe;
    private String recipeTitle = "";
    private int idUser;
    private String userName = "";
    private int quantity;
    private String reason = "INVENDU";
    private LocalDate lossDate = LocalDate.now();

    private static final DateTimeFormatter HUMAN_DATE =
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);

    private static final String SELECT_JOINED =
            "SELECT l.*, r.title, u.firstname, u.lastname FROM recipe_loss l"
                    + " JOIN recipe r ON r.id_recipe = l.id_recipe"
                    + " LEFT JOIN gotta_taste_user u ON u.id_user = l.id_user";

    public RecipeLoss() {
    }

    public RecipeLoss(int idRecipe, int idUser, int quantity, String reason, LocalDate lossDate) {
        this.idRecipe = idRecipe;
        this.idUser = idUser;
        this.quantity = quantity;
        this.reason = reason;
        this.lossDate = lossDate;
    }

    /** Pertes filtrées ; 0 et {@code null} valent « critère non renseigné ». */
    public static ArrayList<RecipeLoss> search(int idRecipe, String reason, LocalDate minDate, LocalDate maxDate)
            throws Exception {
        ArrayList<RecipeLoss> losses = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_JOINED + " WHERE 1 = 1");
        if (idRecipe != 0) {
            sql.append(" AND l.id_recipe = ?");
        }
        if (reason != null && !reason.isBlank()) {
            sql.append(" AND l.reason = ?");
        }
        if (minDate != null) {
            sql.append(" AND l.loss_date >= ?");
        }
        if (maxDate != null) {
            sql.append(" AND l.loss_date <= ?");
        }
        sql.append(" ORDER BY l.loss_date DESC, l.id_recipe_loss DESC");

        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int index = 1;
            if (idRecipe != 0) {
                statement.setInt(index++, idRecipe);
            }
            if (reason != null && !reason.isBlank()) {
                statement.setString(index++, reason);
            }
            if (minDate != null) {
                statement.setDate(index++, Date.valueOf(minDate));
            }
            if (maxDate != null) {
                statement.setDate(index++, Date.valueOf(maxDate));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    losses.add(fromRow(resultSet));
                }
            }
        }
        return losses;
    }

    public void create() throws Exception {
        if (!REASONS.containsKey(reason)) {
            throw new IllegalArgumentException("Motif de perte inconnu : " + reason);
        }
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO recipe_loss (id_recipe, id_user, quantity, reason, loss_date)"
                                + " VALUES (?, ?, ?, ?, ?)")) {
            statement.setInt(1, idRecipe);
            if (idUser == 0) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, idUser);
            }
            statement.setInt(3, quantity);
            statement.setString(4, reason);
            statement.setDate(5, Date.valueOf(lossDate));
            statement.executeUpdate();
        }
    }

    private static RecipeLoss fromRow(ResultSet resultSet) throws Exception {
        RecipeLoss loss = new RecipeLoss();
        loss.id = resultSet.getInt("id_recipe_loss");
        loss.idRecipe = resultSet.getInt("id_recipe");
        loss.recipeTitle = resultSet.getString("title");
        loss.idUser = resultSet.getInt("id_user");
        String firstname = resultSet.getString("firstname");
        loss.userName = firstname == null ? "" : firstname + " " + resultSet.getString("lastname");
        loss.quantity = resultSet.getInt("quantity");
        loss.reason = resultSet.getString("reason");
        loss.lossDate = resultSet.getDate("loss_date").toLocalDate();
        return loss;
    }

    public int getId() {
        return id;
    }

    public int getIdRecipe() {
        return idRecipe;
    }

    public void setIdRecipe(int idRecipe) {
        this.idRecipe = idRecipe;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public String getUserName() {
        return userName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getReason() {
        return reason;
    }

    /** Libellé du motif tel qu'affiché à l'utilisateur. */
    public String getReasonLabel() {
        return REASONS.getOrDefault(reason, reason);
    }

    public LocalDate getLossDate() {
        return lossDate;
    }

    public String getFormattedLossDate() {
        return lossDate.toString();
    }

    public String getHumanFormattedLossDate() {
        return lossDate.format(HUMAN_DATE);
    }
}
