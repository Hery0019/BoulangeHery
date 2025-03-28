package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class RecipeStock {

    private int id;
    private int idRecipe = 1;
    private int reste = 0;

    public RecipeStock(int id, int idRecipe, int reste) {
        this.id = id;
        this.idRecipe = idRecipe;
        this.reste = reste;
    }


    public RecipeStock(int idRecipe, int reste) {
        this.idRecipe = idRecipe;
        this.reste = reste;
    }


    public static ArrayList<RecipeStock> all() throws Exception {
        ArrayList<RecipeStock> recipeStocks = new ArrayList<RecipeStock>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();
            statement = connection.prepareStatement(
                "SELECT * FROM recipe_stock"
            );
            resultSet = statement.executeQuery();

            int id;
            int idRecipe;
            int reste;

            while (resultSet.next()) {
                id = resultSet.getInt("id_vendeur");
                idRecipe = resultSet.getInt("id_recipe");
                reste = resultSet.getInt("reste");

                recipeStocks.add(
                    new RecipeStock(id, idRecipe, reste)
                );
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

        return recipeStocks;
    }


    // public void create() throws Exception {
    //     Connection connection = null;
    //     PreparedStatement statement = null;
    //     try {
    //         connection = DBConnection.getPostgesConnection();
    //         connection.setAutoCommit(false);
    //         statement = connection.prepareStatement(
    //             "INSERT INTO gotta_taste_user(firstname, lastname, email, user_password)"
    //             + " VALUES (?, ?, ?, ?)"
    //         );
    //         statement.setString(1, this.firstname);
    //         statement.setString(2, this.lastname);
    //         statement.setString(3, this.email);
    //         statement.setString(4, this.password);
    //         statement.execute();
    //         connection.commit();
    //     } catch (Exception e) {
    //         connection.rollback();
    //         throw e;
    //     } finally {
    //         statement.close();
    //         connection.close();
    //     }
    // }

    // public void findByEmailAndPassword() throws Exception {
    //     Connection connection = null;
    //     PreparedStatement statement = null;
    //     ResultSet resultSet = null;

    //     try {
    //         connection = DBConnection.getPostgesConnection();
    //         statement = connection.prepareStatement(
    //             "SELECT * FROM gotta_taste_user"
    //             + " WHERE email = ? AND user_password = ?"
    //         );
    //         statement.setString(1, this.email);
    //         statement.setString(2, this.password);
    //         resultSet = statement.executeQuery();

    //         while (resultSet.next()) {
    //             id = resultSet.getInt("id_user");
    //             firstname = resultSet.getString("firstname");
    //             lastname = resultSet.getString("lastname");
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
    // }

    // private String hashPassword(String password) {
    //     MessageDigest md = null;
    //     byte[] hash = null;

    //     try {
    //         md = MessageDigest.getInstance("SHA-256");
    //         hash = md.digest(password.getBytes());
    //     } catch (NoSuchAlgorithmException e) {
    //         e.printStackTrace();
    //     }

    //     StringBuilder sb = new StringBuilder();
    //     for (byte b : hash) {
    //         sb.append(String.format("%02x", b));
    //     }
    //     return sb.toString();
    // }

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

    public int getReste() {
        return reste;
    }

    public void setReste(int reste) {
        this.reste = reste;
    }

}