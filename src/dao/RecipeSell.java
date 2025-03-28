package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private int combien = 0;
    private double argent = 0.0;
    private double reste = 0.0;
    private LocalDate sellDate = LocalDate.now();

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter humanDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);

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
            statement = connection.prepareStatement("SELECT * FROM recipe_sell");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id_recipe_sell");
                int idRecipe = resultSet.getInt("id_recipe");
                int idCategory = resultSet.getInt("id_category");
                int idUser = resultSet.getInt("id_user");
                int combien = resultSet.getInt("combien");
                double argent = resultSet.getDouble("argent");
                double reste = resultSet.getDouble("reste");
                LocalDate sellDate = resultSet.getDate("sell_date").toLocalDate();

                recipeSells.add(
                        new RecipeSell(id, idRecipe, idCategory, idUser, combien, argent, reste, sellDate));
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
                id = resultSet.getInt("id_recipe");
                idRecipe = resultSet.getInt("id_recipe");
                idVendeur = resultSet.getInt("id_vendeur");
                idCategory = resultSet.getInt("id_category");
                idUser = resultSet.getInt("id_user");
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
                    "INSERT INTO recipe_sell(id_vendeur, id_recipe, id_category, id_user, combien, argent, reste, sell_date)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, idVendeur);
            statement.setInt(2, idRecipe);
            statement.setInt(3, idCategory);
            statement.setInt(4, idUser);
            statement.setInt(5, combien);
            statement.setDouble(6, argent);
            statement.setDouble(7, 0.0); // calculer a partir des triggers
            statement.setDate(8, Date.valueOf(sellDate));
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
                    "UPDATE recipe_sell"
                            + " SET id_recipe = ?, id_category = ?, id_user = ?, combien = ?, argent = ?, reste = ?, sell_date = ?"
                            + " WHERE id_recipe_sell = ?");
            statement.setInt(1, idRecipe);
            statement.setInt(2, idCategory);
            statement.setInt(3, idUser);
            statement.setDouble(4, combien);
            statement.setDouble(5, argent);
            statement.setDouble(6, 0.0); // calculer a partir du trigger
            statement.setDate(7, Date.valueOf(sellDate));
            statement.setInt(8, id);
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

    public static ArrayList<RecipeSell> search(
        int searchIdRecipe,
        int searchIdCategory,
        int searchIdUser,
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

            StringBuilder sql = new StringBuilder("SELECT * FROM recipe_sell WHERE 1=1");

            StringBuilder fullQuery = new StringBuilder(sql);

            if (searchIdRecipe != 0) {
                sql.append(" AND id_recipe = ?");
                fullQuery.append(" AND id_recipe = ").append(searchIdRecipe);
            }
            if (searchIdCategory != 0) {
                sql.append(" AND id_category = ?");
                fullQuery.append(" AND id_category = ").append(searchIdCategory);
            }
            if (searchIdUser != 0) {
                sql.append(" AND id_user = ?");
                fullQuery.append(" AND id_user = ").append(searchIdUser);
            }
            if (minCombien != 0) {
                sql.append(" AND combien >= ?");
                fullQuery.append(" AND combien >= ").append(minCombien);
            }
            if (maxCombien != 0) {
                sql.append(" AND combien <= ?");
                fullQuery.append(" AND combien <= ").append(maxCombien);
            }
            if (minArgent != 0.0) {
                sql.append(" AND argent >= ?");
                fullQuery.append(" AND argent >= ").append(minArgent);
            }
            if (maxArgent != 0.0) {
                sql.append(" AND argent <= ?");
                fullQuery.append(" AND argent <= ").append(maxArgent);
            }
            if (minReste != 0.0) {
                sql.append(" AND reste >= ?");
                fullQuery.append(" AND reste >= ").append(minReste);
            }
            if (maxReste != 0.0) {
                sql.append(" AND reste <= ?");
                fullQuery.append(" AND reste <= ").append(maxReste);
            }
            if (minSellDate != null) {
                sql.append(" AND sell_date >= ?");
                fullQuery.append(" AND sell_date >= '").append(minSellDate).append("'");
            }
            if (maxSellDate != null) {
                sql.append(" AND sell_date <= ?");
                fullQuery.append(" AND sell_date <= '").append(maxSellDate).append("'");
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
        
            // Afficher la requête SQL complète
            System.out.println("Requête SQL exécutée : " + fullQuery.toString());
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id_recipe_sell");
                int idRecipe = resultSet.getInt("id_recipe");
                int idCategory = resultSet.getInt("id_category");
                int idUser = resultSet.getInt("id_user");
                int combien = resultSet.getInt("combien");
                double argent = resultSet.getDouble("argent");
                double reste = resultSet.getDouble("reste");
                LocalDate sellDate = resultSet.getDate("sell_date").toLocalDate();
                 // Affichage des données extraites
                System.out.println("id=" + id + 
                ", idRecipe=" + idRecipe + 
                ", idCategory=" + idCategory + 
                ", idUser=" + idUser + 
                ", combien=" + combien + 
                ", argent=" + argent + 
                ", reste=" + reste + 
                ", sellDate=" + sellDate);

                recipeSells.add(new RecipeSell(id, idRecipe, idCategory, idUser, combien, argent, reste, sellDate));
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
