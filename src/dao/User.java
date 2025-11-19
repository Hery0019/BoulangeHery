package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import util.PasswordHasher;

/**
 * Compte utilisateur (gotta_taste_user). L'objet ne porte jamais le mot de
 * passe : il est haché à la création et vérifié uniquement dans
 * {@link #authenticate(String, String)}.
 */
public class User {

    /** Tous les droits. */
    public static final String ADMIN = "ADMIN";
    /** Catalogue : recettes, étapes, ingrédients, catégories, stock. */
    public static final String BOULANGER = "BOULANGER";
    /** Caisse : ventes et commissions. */
    public static final String VENDEUR = "VENDEUR";

    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String role = ADMIN;

    public User(int id, String firstname, String lastname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public User(int id, String firstname, String lastname, String email, String role) {
        this(id, firstname, lastname, email);
        this.role = role;
    }

    public static ArrayList<User> all() throws Exception {
        ArrayList<User> users = new ArrayList<>();
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT id_user, firstname, lastname, email, role FROM gotta_taste_user ORDER BY id_user");
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(fromRow(resultSet));
            }
        }
        return users;
    }

    public static User findById(int id) throws Exception {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT id_user, firstname, lastname, email, role FROM gotta_taste_user WHERE id_user = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? fromRow(resultSet) : null;
            }
        }
    }

    /**
     * Renvoie l'utilisateur si l'email existe et que le mot de passe correspond
     * à l'empreinte stockée, sinon {@code null}. Un mot de passe stocké en
     * clair (base non migrée) ne permet jamais de se connecter.
     */
    public static User authenticate(String email, String password) throws Exception {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT id_user, firstname, lastname, email, role, user_password FROM gotta_taste_user WHERE email = ?")) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    if (PasswordHasher.verify(password, resultSet.getString("user_password"))) {
                        return fromRow(resultSet);
                    }
                }
            }
        }
        return null;
    }

    /** Crée le compte avec le mot de passe haché et renvoie son identifiant. */
    /** Crée un compte avec le rôle {@link #ADMIN}. */
    public static int create(String firstname, String lastname, String email, String password) throws Exception {
        return create(firstname, lastname, email, password, ADMIN);
    }

    public static int create(String firstname, String lastname, String email, String password, String role)
            throws Exception {
        if (!ADMIN.equals(role) && !BOULANGER.equals(role) && !VENDEUR.equals(role)) {
            throw new IllegalArgumentException("Rôle inconnu : " + role
                    + " (attendu " + ADMIN + ", " + BOULANGER + " ou " + VENDEUR + ")");
        }
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO gotta_taste_user(firstname, lastname, email, user_password, role) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, firstname);
            statement.setString(2, lastname);
            statement.setString(3, email);
            statement.setString(4, PasswordHasher.hash(password));
            statement.setString(5, role);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw DuplicateEntityException.orSame(e, "Un compte existe déjà avec cet email.");
        }
    }

    private static User fromRow(ResultSet resultSet) throws Exception {
        return new User(
                resultSet.getInt("id_user"),
                resultSet.getString("firstname"),
                resultSet.getString("lastname"),
                resultSet.getString("email"),
                resultSet.getString("role"));
    }

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

    /** Rôle du compte : {@link #ADMIN}, {@link #BOULANGER} ou {@link #VENDEUR}. */
    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", firstname=" + firstname + ", lastname=" + lastname + ", email=" + email + "]";
    }

}
