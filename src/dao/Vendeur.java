package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Vendeur {

    private int id;
    private String firstname;
    private String lastname;
    private String sexe;
    private String email;
    private double salary;

    public Vendeur(int id, String firstname, String lastname, String email, double salary) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        setSalary(salary);
    }

    public Vendeur(String firstname, String lastname, String email, double salary) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        setSalary(salary);
    }

    public Vendeur(int id, String firstname, String sexe, String lastname, String email, double salary) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.sexe = sexe;
        this.email = email;
        setSalary(salary);
    }

    public Vendeur(String firstname, String sexe, String lastname, String email, double salary) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.sexe = sexe;
        this.email = email;
        setSalary(salary);
    }


    public static ArrayList<Vendeur> all() throws Exception {
        ArrayList<Vendeur> vendeurs = new ArrayList<Vendeur>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getPostgesConnection();
            statement = connection.prepareStatement(
                "SELECT * FROM vendeur"
            );
            resultSet = statement.executeQuery();

            int id;
            String firstname;
            String lastname;
            String email;
            String sexe;
            double salary;
            while (resultSet.next()) {
                id = resultSet.getInt("id_vendeur");
                firstname = resultSet.getString("firstname");
                lastname = resultSet.getString("lastname");
                sexe = resultSet.getString("sexe");
                email = resultSet.getString("email");
                salary = resultSet.getDouble("salary");

                vendeurs.add(
                    new Vendeur(id, firstname, sexe, lastname, email, salary)
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

        return vendeurs;
    }

    public static Vendeur findById(int id) throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
    
        try {
            connection = DBConnection.getPostgesConnection();
            statement = connection.prepareStatement(
                    "SELECT * FROM vendeur WHERE id_vendeur = ?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                String email = resultSet.getString("email");
                String sexe = resultSet.getString("sexe");
                double salary = resultSet.getDouble("salary");
    
                return new Vendeur(id, firstname, lastname, email, salary);
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
    
        return null; // Retourne null si aucun utilisateur n'a été trouvé
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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFullName() {
        return this.firstname + " " + this.lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
   

}