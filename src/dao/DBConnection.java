package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Accès à la base PostgreSQL.
 *
 * La configuration vient de l'environnement, jamais du code :
 * <ul>
 *   <li>{@code BOULANGERIE_DB_URL}      — ex. {@code jdbc:postgresql://localhost:5432/gotta_taste}</li>
 *   <li>{@code BOULANGERIE_DB_USER}     — rôle PostgreSQL dédié à l'application (pas le superutilisateur)</li>
 *   <li>{@code BOULANGERIE_DB_PASSWORD}</li>
 * </ul>
 * Les propriétés système {@code boulangerie.db.url|user|password} (option
 * {@code -D} de la JVM, ex. dans {@code setenv.bat} de Tomcat) ont priorité sur
 * les variables d'environnement.
 */
public class DBConnection {

    public static final String ENV_URL = "BOULANGERIE_DB_URL";
    public static final String ENV_USER = "BOULANGERIE_DB_USER";
    public static final String ENV_PASSWORD = "BOULANGERIE_DB_PASSWORD";

    public static Connection getPostgesConnection() throws SQLException {
        return DriverManager.getConnection(
                setting(ENV_URL, "boulangerie.db.url"),
                setting(ENV_USER, "boulangerie.db.user"),
                setting(ENV_PASSWORD, "boulangerie.db.password"));
    }

    private static String setting(String envName, String propertyName) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            value = System.getenv(envName);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Configuration manquante : définir la variable d'environnement "
                    + envName + " (ou la propriété système " + propertyName + ")");
        }
        return value;
    }

}
