package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class Commission {

    private int id;
    private int idVendeur = 1;
    private int idRecipe = 1; // Nouvel attribut
    private double commissionAmount = 0.0;
    private LocalDate commissionDate = LocalDate.now();
    private String vendeurSexe = "";

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter humanDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy",
            Locale.FRENCH);

    public Commission() {
    }

    public Commission(int id) {
        this.id = id;
    }

    public Commission(int idVendeur, int idRecipe, double commissionAmount,
            LocalDate commissionDate) {
        this.idVendeur = idVendeur;
        this.idRecipe = idRecipe;
        this.commissionDate = commissionDate;
    }

    public Commission(int id,  int idVendeur, int idRecipe, double commissionAmount, LocalDate commissionDate) {
        this.id = id;
        this.idVendeur = idVendeur;
        this.idRecipe = idRecipe;
        this.commissionAmount = commissionAmount;
        this.commissionDate = commissionDate;
    }

    public Commission(int idVendeur, int idRecipe, double commissionAmount,
            LocalDate commissionDate, String vendeurSexe) {
        this.idVendeur = idVendeur;
        this.idRecipe = idRecipe;
        this.commissionDate = commissionDate;
        this.vendeurSexe = vendeurSexe;
    }

    public Commission(int id,  int idVendeur, int idRecipe, double commissionAmount, LocalDate commissionDate,  String vendeurSexe) {
        this.id = id;
        this.idVendeur = idVendeur;
        this.idRecipe = idRecipe;
        this.commissionAmount = commissionAmount;
        this.commissionDate = commissionDate;
        this.vendeurSexe = vendeurSexe;
    }



    public static ArrayList<Commission> all() throws Exception {
        ArrayList<Commission> commissions = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();
            statement = connection.prepareStatement("SELECT * FROM commission");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id_commission");
                int idVendeur = resultSet.getInt("id_vendeur");
                int idRecipe = resultSet.getInt("id_recipe");
                LocalDate commissionDate = resultSet.getDate("commission_date").toLocalDate();
                double commissionAmount = resultSet.getDouble("commission_amount");

                commissions.add(
                        new Commission(id, idVendeur, idRecipe, commissionAmount, commissionDate));
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

        return commissions;
    }

    

    public static ArrayList<Commission> search(
            int searchidVendeur,
            int searchidRecipe,
            LocalDate minCommissionDate,
            LocalDate maxCommissionDate,
            double commissionAmount,
            String sexeVendeur) throws Exception {

        ArrayList<Commission> commissions = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();

            StringBuilder sql = new StringBuilder(
                    // Libellés joints : la liste rappelait findById pour le vendeur et la
                    // recette de chaque ligne, soit deux requêtes par commission.
                    "SELECT c.*, v.sexe, v.firstname || ' ' || v.lastname AS vendeur_name, r.title" +
                    " FROM commission c" +
                    " JOIN vendeur v ON c.id_vendeur = v.id_vendeur" +
                    " JOIN recipe r ON r.id_recipe = c.id_recipe WHERE 1=1");

            if (searchidVendeur != 0) {
                sql.append(" AND c.id_vendeur = ?");
            }
            if (searchidRecipe != 0) {
                sql.append(" AND c.id_recipe = ?");
            }
            if (minCommissionDate != null) {
                sql.append(" AND c.commission_date >= ?");
            }
            if (maxCommissionDate != null) {
                sql.append(" AND c.commission_date <= ?");
            }
            if (commissionAmount != 0.0) {
                sql.append(" AND c.commission_amount = ?");
            }
            if (sexeVendeur != null && !sexeVendeur.isEmpty()) {
                sql.append(" AND v.sexe = ?");
            }

            sql.append(" ORDER BY c.id_commission ASC");
            statement = connection.prepareStatement(sql.toString());

            int paramIndex = 1;
            if (searchidVendeur != 0) {
                statement.setInt(paramIndex++, searchidVendeur);
            }
            if (searchidRecipe != 0) {
                statement.setInt(paramIndex++, searchidRecipe);
            }
            if (minCommissionDate != null) {
                statement.setDate(paramIndex++, Date.valueOf(minCommissionDate));
            }
            if (maxCommissionDate != null) {
                statement.setDate(paramIndex++, Date.valueOf(maxCommissionDate));
            }
            if (commissionAmount != 0.0) {
                statement.setDouble(paramIndex++, commissionAmount);
            }
            if (sexeVendeur != null && !sexeVendeur.isEmpty()) {
                statement.setString(paramIndex++, sexeVendeur);
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id_commission");
                int idVendeur = resultSet.getInt("id_vendeur");
                int idRecipe = resultSet.getInt("id_recipe");
                LocalDate commissionDate = resultSet.getDate("commission_date").toLocalDate();
                double searchCommissionAmount = resultSet.getDouble("commission_amount");
                String vendeurSexe = resultSet.getString("sexe");

                Commission commission =
                        new Commission(id, idVendeur, idRecipe, searchCommissionAmount, commissionDate, vendeurSexe);
                commission.setVendeurName(resultSet.getString("vendeur_name"));
                commission.setRecipeTitle(resultSet.getString("title"));
                commissions.add(commission);
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

        return commissions;
    }

  
    

    /** Libellés lus par la même requête que la commission. */
    private String vendeurName = "";
    private String recipeTitle = "";

    public String getVendeurSexe() {
        return vendeurSexe;
    }

    public String getVendeurName() {
        return vendeurName;
    }

    public void setVendeurName(String vendeurName) {
        this.vendeurName = vendeurName;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
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

    public double getCommissionsAmount() {
        return this.commissionAmount;
    }

    public void setCommissionAmount(double commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public void setIdRecipe(int idRecipe) {
        this.idRecipe = idRecipe;
    }


    public int getIdVendeur() {
        return idVendeur;
    }

    public void setIdVendeur(int idVendeur) {
        this.idVendeur = idVendeur;
    }

    public LocalDate getCommissionDate() {
        return commissionDate;
    }

    public String getFormattedcommissionDate() {
        return commissionDate.format(dateFormatter);
    }

    public String getHumanFormattedcommissionDate() {
        return commissionDate.format(humanDateFormatter);
    }

    public void setCommissionDate(LocalDate commissionDate) {
        this.commissionDate = commissionDate;
    }
}
