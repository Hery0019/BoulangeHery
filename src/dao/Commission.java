package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Locale;

public class Commission {

    private int id;
    private int idVendeur = 1;
    private int idRecipe = 1; // Nouvel attribut
    private double commissionAmount = 0.0;
    private LocalDate commissionDate = LocalDate.now();
    private String vendeurSexe = "";

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

    

    // public static Commission findById(int id) throws Exception {
    //     Connection connection = null;
    //     PreparedStatement statement = null;
    //     ResultSet resultSet = null;
    
    //     try {
    //         connection = DBConnection.getPostgesConnection();
    //         statement = connection.prepareStatement(
    //                 "SELECT * FROM commission WHERE id_commission = ?");
    //         statement.setInt(1, id);
    //         resultSet = statement.executeQuery();
    
    //         if (resultSet.next()) {
    //             Commission Commission = new Commission();
    //             Commission.setId(resultSet.getInt("id_commission"));
    //             Commission.setTitle(resultSet.getString("title"));
    //             Commission.setDescription(resultSet.getString("Commission_description"));
    //             Commission.setidVendeur(resultSet.getInt("id_category"));
    //             Commission.setidRecipe(resultSet.getInt("id_perfume"));
    //             Commission.setCookTime(resultSet.getTime("cook_time").toLocalTime());
    //             Commission.setCreatedBy(resultSet.getString("created_by"));
    //             Commission.setcommissionDate(resultSet.getDate("created_date").toLocalDate());
    //             Commission.setcommissionAmount(resultSet.getDouble("commissionAmount"));
    //             return Commission;
    //         }
    //     } catch (Exception e) {
    //         throw e;
    //     } finally {
    //         if (resultSet != null) {
    //             resultSet.close();
    //         }
    //         if (statement != null) {
    //             statement.close();
    //         }
    //         if (connection != null) {
    //             connection.close();
    //         }
    //     }
    
    //     return null; // Retourne null si aucune recette n'a été trouvée
    // }
    
    

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
                    "SELECT c.*, v.sexe FROM commission c " +
                    "JOIN vendeur v ON c.id_vendeur = v.id_vendeur WHERE 1=1");
            StringBuilder fullQuery = new StringBuilder(sql);

            if (searchidVendeur != 0) {
                sql.append(" AND c.id_vendeur = ?");
                fullQuery.append(" AND c.id_vendeur = ").append(searchidVendeur);
            }
            if (searchidRecipe != 0) {
                sql.append(" AND c.id_recipe = ?");
                fullQuery.append(" AND c.id_recipe = ").append(searchidRecipe);
            }
            if (minCommissionDate != null) {
                sql.append(" AND c.commission_date >= ?");
                fullQuery.append(" AND c.commission_date >= '").append(minCommissionDate).append("'");
            }
            if (maxCommissionDate != null) {
                sql.append(" AND c.commission_date <= ?");
                fullQuery.append(" AND c.commission_date <= '").append(maxCommissionDate).append("'");
            }
            if (commissionAmount != 0.0) {
                sql.append(" AND c.commission_amount = ?");
                fullQuery.append(" AND c.commission_amount = ").append(commissionAmount);
            }
            if (sexeVendeur != null && !sexeVendeur.isEmpty()) {
                sql.append(" AND v.sexe = ?");
                fullQuery.append(" AND v.sexe = '").append(sexeVendeur).append("'");
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

            // Afficher la requête SQL complète
            System.out.println("Requête SQL exécutée : " + fullQuery.toString());
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id_commission");
                int idVendeur = resultSet.getInt("id_vendeur");
                int idRecipe = resultSet.getInt("id_recipe");
                LocalDate commissionDate = resultSet.getDate("commission_date").toLocalDate();
                double searchCommissionAmount = resultSet.getDouble("commission_amount");
                String vendeurSexe = resultSet.getString("sexe");

                commissions.add(new Commission(id, idVendeur, idRecipe, searchCommissionAmount, commissionDate, vendeurSexe));
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
