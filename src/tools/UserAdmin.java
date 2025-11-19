package tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;

import dao.DBConnection;
import dao.User;
import util.PasswordHasher;

/**
 * Outil en ligne de commande pour gérer les comptes (remplace l'ancien Main.java
 * qui créait un compte codé en dur).
 *
 * <pre>
 *   java -cp "WEB-INF/classes;lib/*" tools.UserAdmin hash &lt;mot de passe&gt;
 *   java -cp "WEB-INF/classes;lib/*" tools.UserAdmin create &lt;prénom&gt; &lt;nom&gt; &lt;email&gt; &lt;mot de passe&gt; [rôle]
 *   java -cp "WEB-INF/classes;lib/*" tools.UserAdmin role &lt;email&gt; &lt;ADMIN|BOULANGER|VENDEUR&gt;
 *   java -cp "WEB-INF/classes;lib/*" tools.UserAdmin list
 *   java -cp "WEB-INF/classes;lib/*" tools.UserAdmin rehash
 * </pre>
 *
 * {@code rehash} convertit les mots de passe encore stockés en clair (bases
 * antérieures à la migration 004) en empreintes PBKDF2. À exécuter une fois.
 * Les commandes qui accèdent à la base lisent la configuration décrite dans
 * {@link DBConnection}.
 */
public class UserAdmin {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            usage();
            return;
        }
        switch (args[0]) {
            case "hash":
                requireArgs(args, 2);
                System.out.println(PasswordHasher.hash(args[1]));
                break;
            case "create":
                requireArgs(args, 5);
                // Rôle facultatif en 5e argument, ADMIN par défaut.
                String role = args.length > 5 ? args[5].toUpperCase(Locale.ROOT) : User.ADMIN;
                int id = User.create(args[1], args[2], args[3], args[4], role);
                System.out.println("Utilisateur créé, id_user = " + id + ", rôle " + role);
                break;
            case "role":
                requireArgs(args, 3);
                System.out.println(changeRole(args[1], args[2].toUpperCase(Locale.ROOT))
                        ? "Rôle mis à jour."
                        : "Aucun compte avec cet email.");
                break;
            case "list":
                listUsers();
                break;
            case "rehash":
                System.out.println(rehashPlaintextPasswords() + " mot(s) de passe converti(s).");
                break;
            default:
                usage();
        }
    }

    /** Change le rôle d'un compte ; renvoie faux si l'email est inconnu. */
    private static boolean changeRole(String email, String role) throws Exception {
        if (!User.ADMIN.equals(role) && !User.BOULANGER.equals(role) && !User.VENDEUR.equals(role)) {
            throw new IllegalArgumentException("Rôle inconnu : " + role);
        }
        try (Connection connection = DBConnection.getPostgesConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE gotta_taste_user SET role = ? WHERE email = ?")) {
            statement.setString(1, role);
            statement.setString(2, email);
            return statement.executeUpdate() > 0;
        }
    }

    private static void listUsers() throws Exception {
        for (User user : User.all()) {
            System.out.printf("%-4d %-30s %-30s %s%n",
                    user.getId(), user.getEmail(), user.getFullName(), user.getRole());
        }
    }

    private static int rehashPlaintextPasswords() throws Exception {
        int converted = 0;
        try (Connection connection = DBConnection.getPostgesConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement select = connection.prepareStatement(
                    "SELECT id_user, user_password FROM gotta_taste_user WHERE user_password NOT LIKE 'pbkdf2$%'");
                    PreparedStatement update = connection.prepareStatement(
                            "UPDATE gotta_taste_user SET user_password = ? WHERE id_user = ?");
                    ResultSet rows = select.executeQuery()) {
                while (rows.next()) {
                    update.setString(1, PasswordHasher.hash(rows.getString("user_password")));
                    update.setInt(2, rows.getInt("id_user"));
                    update.executeUpdate();
                    converted++;
                }
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        }
        return converted;
    }

    private static void requireArgs(String[] args, int count) {
        if (args.length < count) {
            usage();
            System.exit(2);
        }
    }

    private static void usage() {
        System.err.println("Usage :");
        System.err.println("  UserAdmin hash <mot de passe>");
        System.err.println("  UserAdmin create <prénom> <nom> <email> <mot de passe> [ADMIN|BOULANGER|VENDEUR]");
        System.err.println("  UserAdmin role <email> <ADMIN|BOULANGER|VENDEUR>");
        System.err.println("  UserAdmin list");
        System.err.println("  UserAdmin rehash");
    }
}
