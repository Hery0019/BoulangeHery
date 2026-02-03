package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class RecipeSell {

    private int id;
    private int idVendeur = 1;
    private int idRecipe = 1;
    private int idCategory = 1;
    private int idUser = 1;
    /** Acheteur ; 0 pour une vente au comptoir. */
    private int idClient = 0;
    // Libellés lus par la même requête : la liste affichait ces noms en
    // rappelant findById pour chaque ligne, soit quatre requêtes par vente.
    private String recipeTitle = "";
    private String categoryName = "";
    private String clientName = "";
    private String userName = "";
    private String vendeurName = "";
    private int combien = 0;
    private double argent = 0.0;
    private double reste = 0.0;
    private LocalDate sellDate = LocalDate.now();

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter humanDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);

    /** Vente et libellés associés, pour éviter une requête par ligne à l'affichage. */
    private static final String SELECT_JOINED =
            "SELECT s.*, r.title, cat.category_name,"
                    + " COALESCE(cl.firstname || ' ' || cl.lastname, '') AS client_name,"
                    + " COALESCE(u.firstname || ' ' || u.lastname, '') AS user_name,"
                    + " COALESCE(v.firstname || ' ' || v.lastname, '') AS vendeur_name"
                    + " FROM recipe_sell s"
                    + " JOIN recipe r ON r.id_recipe = s.id_recipe"
                    + " JOIN category cat ON cat.id_category = s.id_category"
                    + " LEFT JOIN client cl ON cl.id_client = s.id_client"
                    + " LEFT JOIN gotta_taste_user u ON u.id_user = s.id_user"
                    + " LEFT JOIN vendeur v ON v.id_vendeur = s.id_vendeur";

    public RecipeSell() {
    }

    public RecipeSell(int id) {
        this.id = id;
    }

  // Constructeur avec tous les attributs sauf les statiques
    public RecipeSell(int id, int idRecipe, int idCategory, int idUser, int combien, double argent, double reste, LocalDate sellDate) {
        this.id = id;
        this.idRecipe = idRecipe;
        this.idCategory = idCategory;
        this.idUser = idUser;
        this.combien = combien;
        this.argent = argent;
        this.reste = reste;
        this.sellDate = sellDate != null ? sellDate : LocalDate.now();
    }
    public RecipeSell(int id, int idVendeur, int idRecipe, int idCategory, int idUser, int combien, double argent, double reste, LocalDate sellDate) {
        this.id = id;
        this.idVendeur = idVendeur;
        this.idRecipe = idRecipe;
        this.idCategory = idCategory;
        this.idUser = idUser;
        this.combien = combien;
        this.argent = argent;
        this.reste = reste;
        this.sellDate = sellDate != null ? sellDate : LocalDate.now();
    }

    // Constructeur simplifié avec des valeurs par défaut pour certains attributs
    public RecipeSell(int id, int combien, double argent, double reste) {
        this.id = id;
        this.combien = combien;
        this.argent = argent;
        this.reste = reste;
    }

    public RecipeSell(int id, int idVendeur, int combien, double argent, double reste) {
        this.id = id;
        this.idVendeur = idVendeur;
        this.combien = combien;
        this.argent = argent;
        this.reste = reste;
    }

    public static ArrayList<RecipeSell> all() throws Exception {
        ArrayList<RecipeSell> recipeSells = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();
            statement = connection.prepareStatement(SELECT_JOINED);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id_recipe_sell");
                int idVendeur = resultSet.getInt("id_vendeur");
                int idRecipe = resultSet.getInt("id_recipe");
                int idCategory = resultSet.getInt("id_category");
                int idUser = resultSet.getInt("id_user");
                int combien = resultSet.getInt("combien");
                double argent = resultSet.getDouble("argent");
                double reste = resultSet.getDouble("reste");
                LocalDate sellDate = resultSet.getDate("sell_date").toLocalDate();

                RecipeSell recipeSell =
                        new RecipeSell(id, idVendeur, idRecipe, idCategory, idUser, combien, argent, reste, sellDate);
                recipeSell.idClient = resultSet.getInt("id_client");
                recipeSell.recipeTitle = resultSet.getString("title");
                recipeSell.categoryName = resultSet.getString("category_name");
                recipeSell.clientName = resultSet.getString("client_name");
                recipeSell.userName = resultSet.getString("user_name");
                recipeSell.vendeurName = resultSet.getString("vendeur_name");
                recipeSells.add(recipeSell);
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

        return recipeSells;
    }

    public void find() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();
            statement = connection.prepareStatement(
                    "SELECT * FROM recipe_sell WHERE id_recipe_sell = ?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                id = resultSet.getInt("id_recipe_sell");
                idRecipe = resultSet.getInt("id_recipe");
                idVendeur = resultSet.getInt("id_vendeur");
                idCategory = resultSet.getInt("id_category");
                idUser = resultSet.getInt("id_user");
                idClient = resultSet.getInt("id_client");
                combien = resultSet.getInt("combien");
                argent = resultSet.getDouble("argent");
                reste = resultSet.getDouble("reste");
                sellDate = resultSet.getDate("sell_date").toLocalDate();
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

    public void create() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getPostgesConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(
                    "INSERT INTO recipe_sell(id_vendeur, id_recipe, id_category, id_user, id_client, combien, argent, reste, sell_date)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, idVendeur);
            statement.setInt(2, idRecipe);
            statement.setInt(3, idCategory);
            statement.setInt(4, idUser);
            setClient(statement, 5);
            statement.setInt(6, combien);
            statement.setDouble(7, argent);
            statement.setDouble(8, 0.0); // calculé par le trigger calculate_reste
            statement.setDate(9, Date.valueOf(sellDate));
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
            statement = connection.prepareStatement(
                    "UPDATE recipe_sell"
                            + " SET id_vendeur = ?, id_recipe = ?, id_category = ?, id_user = ?, id_client = ?, combien = ?, argent = ?, sell_date = ?"
                            + " WHERE id_recipe_sell = ?");
            statement.setInt(1, idVendeur);
            statement.setInt(2, idRecipe);
            statement.setInt(3, idCategory);
            statement.setInt(4, idUser);
            setClient(statement, 5);
            statement.setInt(6, combien);
            statement.setDouble(7, argent);
            statement.setDate(8, Date.valueOf(sellDate));
            statement.setInt(9, id); // reste est recalculé par le trigger calculate_reste
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

    /** Une vente et ses libellés, ou {@code null} si l'identifiant n'existe pas. */
    public static RecipeSell findById(int id) throws Exception {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        SELECT_JOINED + " WHERE s.id_recipe_sell = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                RecipeSell sell = new RecipeSell(
                        resultSet.getInt("id_recipe_sell"),
                        resultSet.getInt("id_vendeur"),
                        resultSet.getInt("id_recipe"),
                        resultSet.getInt("id_category"),
                        resultSet.getInt("id_user"),
                        resultSet.getInt("combien"),
                        resultSet.getDouble("argent"),
                        resultSet.getDouble("reste"),
                        resultSet.getDate("sell_date").toLocalDate());
                sell.idClient = resultSet.getInt("id_client");
                sell.recipeTitle = resultSet.getString("title");
                sell.categoryName = resultSet.getString("category_name");
                sell.clientName = resultSet.getString("client_name");
                sell.userName = resultSet.getString("user_name");
                sell.vendeurName = resultSet.getString("vendeur_name");
                return sell;
            }
        }
    }

    /**
     * Prix unitaire pratiqué lors de la vente, reconstitué depuis les montants :
     * {@code reste = argent - combien x prix}. Le prix courant de la recette a
     * pu changer depuis, le ticket doit montrer celui du jour de la vente.
     */
    public double getUnitPrice() {
        return combien == 0 ? 0.0 : (argent - reste) / combien;
    }

    /** Montant réellement dû pour cette vente. */
    public double getTotal() {
        return argent - reste;
    }

    public static ArrayList<RecipeSell> search(
        int searchIdRecipe,
        int searchIdCategory,
        int searchIdUser,
        int searchIdClient,
        int minCombien,
        int maxCombien,
        double minArgent,
        double maxArgent,
        double minReste,
        double maxReste,
        LocalDate minSellDate,
        LocalDate maxSellDate) throws Exception {

        ArrayList<RecipeSell> recipeSells = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();

            StringBuilder sql = new StringBuilder(SELECT_JOINED + " WHERE 1=1");


            if (searchIdRecipe != 0) {
                sql.append(" AND s.id_recipe = ?");
            }
            if (searchIdCategory != 0) {
                sql.append(" AND s.id_category = ?");
            }
            if (searchIdUser != 0) {
                sql.append(" AND s.id_user = ?");
            }
            if (searchIdClient != 0) {
                sql.append(" AND s.id_client = ?");
            }
            if (minCombien != 0) {
                sql.append(" AND s.combien >= ?");
            }
            if (maxCombien != 0) {
                sql.append(" AND s.combien <= ?");
            }
            if (minArgent != 0.0) {
                sql.append(" AND s.argent >= ?");
            }
            if (maxArgent != 0.0) {
                sql.append(" AND s.argent <= ?");
            }
            if (minReste != 0.0) {
                sql.append(" AND s.reste >= ?");
            }
            if (maxReste != 0.0) {
                sql.append(" AND s.reste <= ?");
            }
            if (minSellDate != null) {
                sql.append(" AND s.sell_date >= ?");
            }
            if (maxSellDate != null) {
                sql.append(" AND s.sell_date <= ?");
            }
        
            sql.append(" ORDER BY id_recipe_sell ASC");
            statement = connection.prepareStatement(sql.toString());
        
            int paramIndex = 1;
        
            if (searchIdRecipe != 0) {
                statement.setInt(paramIndex++, searchIdRecipe);
            }
            if (searchIdCategory != 0) {
                statement.setInt(paramIndex++, searchIdCategory);
            }
            if (searchIdUser != 0) {
                statement.setInt(paramIndex++, searchIdUser);
            }
            if (searchIdClient != 0) {
                statement.setInt(paramIndex++, searchIdClient);
            }
            if (minCombien != 0) {
                statement.setInt(paramIndex++, minCombien);
            }
            if (maxCombien != 0) {
                statement.setInt(paramIndex++, maxCombien);
            }
            if (minArgent != 0.0) {
                statement.setDouble(paramIndex++, minArgent);
            }
            if (maxArgent != 0.0) {
                statement.setDouble(paramIndex++, maxArgent);
            }
            if (minReste != 0.0) {
                statement.setDouble(paramIndex++, minReste);
            }
            if (maxReste != 0.0) {
                statement.setDouble(paramIndex++, maxReste);
            }
            if (minSellDate != null) {
                statement.setDate(paramIndex++, Date.valueOf(minSellDate));
            }
            if (maxSellDate != null) {
                statement.setDate(paramIndex++, Date.valueOf(maxSellDate));
            }
        
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id_recipe_sell");
                int idVendeur = resultSet.getInt("id_vendeur");
                int idRecipe = resultSet.getInt("id_recipe");
                int idCategory = resultSet.getInt("id_category");
                int idUser = resultSet.getInt("id_user");
                int combien = resultSet.getInt("combien");
                double argent = resultSet.getDouble("argent");
                double reste = resultSet.getDouble("reste");
                LocalDate sellDate = resultSet.getDate("sell_date").toLocalDate();

                RecipeSell recipeSell =
                        new RecipeSell(id, idVendeur, idRecipe, idCategory, idUser, combien, argent, reste, sellDate);
                recipeSell.idClient = resultSet.getInt("id_client");
                recipeSell.recipeTitle = resultSet.getString("title");
                recipeSell.categoryName = resultSet.getString("category_name");
                recipeSell.clientName = resultSet.getString("client_name");
                recipeSell.userName = resultSet.getString("user_name");
                recipeSell.vendeurName = resultSet.getString("vendeur_name");
                recipeSells.add(recipeSell);
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

        return recipeSells;
    }

    public void delete() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBConnection.getPostgesConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(
                    "DELETE FROM recipe_sell"
                            + " WHERE id_recipe_sell = ?");
            statement.setInt(1, id);
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

    /** Une vente au comptoir n'a pas de client : la colonne reste NULL. */
    private void setClient(PreparedStatement statement, int index) throws SQLException {
        if (idClient == 0) {
            statement.setNull(index, java.sql.Types.INTEGER);
        } else {
            statement.setInt(index, idClient);
        }
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    /** Nom de l'acheteur, ou « Vente au comptoir » si la vente n'en a pas. */
    public String getClientLabel() {
        return clientName == null || clientName.isBlank() ? "Vente au comptoir" : clientName;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public String getCategoryName() {
        return categoryName;
    }

    /** Compte qui a saisi la vente. */
    public String getUserName() {
        return userName;
    }

    public String getVendeurName() {
        return vendeurName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdRecipe() {
        return idRecipe;
    }

    public void setIdRecipe(int idRecipe) {
        this.idRecipe = idRecipe;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }


    public LocalDate getSellDate() {
        return sellDate;
    }

    public String getFormattedCreatedDate() {
        return sellDate.format(dateFormatter);
    }

    public String getHumanFormattedCreatedDate() {
        return sellDate.format(humanDateFormatter);
    }

    public void setSellDate(LocalDate sellDate) {
        this.sellDate = sellDate;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getCombien() {
        return combien;
    }

    public void setCombien(int combien) {
        this.combien = combien;
    }

    public double getArgent() {
        return argent;
    }

    public void setArgent(double argent) {
        this.argent = argent;
    }

    public double getReste() {
        return reste;
    }

    public void setReste(double reste) {
        this.reste = reste;
    }

    public int getIdVendeur() {
        return idVendeur;
    }

    public void setIdVendeur(int idVendeur) {
        this.idVendeur = idVendeur;
    }
    @Override
    public String toString() {
        return "RecipeSell{" +
                "id=" + id +
                ", idRecipe=" + idRecipe +
                ", idCategory=" + idCategory +
                ", idUser=" + idUser +
                ", combien=" + combien +
                ", argent=" + argent +
                ", reste=" + reste +
                ", sellDate=" + sellDate +
                '}';
    }

}
