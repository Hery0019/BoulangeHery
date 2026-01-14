package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Acheteur. Distinct de {@link User}, qui désigne un compte de l'application :
 * avant la migration 014, la colonne « Client » des ventes affichait les
 * employés.
 */
public class Client {

    private int id;
    private String firstname = "";
    private String lastname = "";
    private String phone = "";
    private String email = "";
    private LocalDate createdDate = LocalDate.now();

    public Client() {
    }

    public Client(int id) {
        this.id = id;
    }

    public Client(int id, String firstname, String lastname, String phone, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
    }

    public static ArrayList<Client> all() throws Exception {
        ArrayList<Client> clients = new ArrayList<>();
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM client ORDER BY lastname, firstname");
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                clients.add(fromRow(resultSet));
            }
        }
        return clients;
    }

    /** Clients dont le nom, le prénom, le téléphone ou l'email contient {@code term}. */
    public static ArrayList<Client> search(String term) throws Exception {
        ArrayList<Client> clients = new ArrayList<>();
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM client"
                                + " WHERE firstname ILIKE ? OR lastname ILIKE ?"
                                + " OR COALESCE(phone, '') ILIKE ? OR COALESCE(email, '') ILIKE ?"
                                + " ORDER BY lastname, firstname")) {
            String pattern = "%" + (term == null ? "" : term) + "%";
            for (int i = 1; i <= 4; i++) {
                statement.setString(i, pattern);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    clients.add(fromRow(resultSet));
                }
            }
        }
        return clients;
    }

    public void find() throws Exception {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM client WHERE id_client = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Client found = fromRow(resultSet);
                    firstname = found.firstname;
                    lastname = found.lastname;
                    phone = found.phone;
                    email = found.email;
                    createdDate = found.createdDate;
                }
            }
        }
    }

    public void create() throws Exception {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO client (firstname, lastname, phone, email) VALUES (?, ?, ?, ?)")) {
            bind(statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw DuplicateEntityException.orSame(e, "Un client utilise déjà cet email.");
        }
    }

    public void update() throws Exception {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE client SET firstname = ?, lastname = ?, phone = ?, email = ? WHERE id_client = ?")) {
            bind(statement);
            statement.setInt(5, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw DuplicateEntityException.orSame(e, "Un client utilise déjà cet email.");
        }
    }

    public void delete() throws Exception {
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM client WHERE id_client = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            if (EntityInUseException.isForeignKeyViolation(e)) {
                throw new EntityInUseException(
                        "Ce client est rattaché à des ventes : il ne peut pas être supprimé.");
            }
            throw e;
        }
    }

    private void bind(PreparedStatement statement) throws SQLException {
        statement.setString(1, firstname);
        statement.setString(2, lastname);
        statement.setString(3, phone == null || phone.isBlank() ? null : phone);
        statement.setString(4, email == null || email.isBlank() ? null : email);
    }

    private static Client fromRow(ResultSet resultSet) throws SQLException {
        Client client = new Client(
                resultSet.getInt("id_client"),
                resultSet.getString("firstname"),
                resultSet.getString("lastname"),
                resultSet.getString("phone"),
                resultSet.getString("email"));
        client.createdDate = resultSet.getDate("created_date").toLocalDate();
        return client;
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
        return (firstname + " " + lastname).trim();
    }

    public String getPhone() {
        return phone == null ? "" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }
}
