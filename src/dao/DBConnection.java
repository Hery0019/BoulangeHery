package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Accès à la base PostgreSQL.
 *
 * Sous Tomcat, les connexions viennent du pool JNDI {@code jdbc/boulangerie}
 * déclaré dans {@code META-INF/context.xml} (paramétré par les propriétés
 * système {@code boulangerie.db.url|user|password}, voir README).
 *
 * Hors conteneur (outils en ligne de commande, tests), repli sur
 * {@link DriverManager} avec la même configuration, lue dans les propriétés
 * système puis dans les variables d'environnement
 * {@code BOULANGERIE_DB_URL|USER|PASSWORD}.
 */
public class DBConnection {

    public static final String JNDI_NAME = "java:comp/env/jdbc/boulangerie";
    public static final String ENV_URL = "BOULANGERIE_DB_URL";
    public static final String ENV_USER = "BOULANGERIE_DB_USER";
    public static final String ENV_PASSWORD = "BOULANGERIE_DB_PASSWORD";

    private static final Logger LOG = Logger.getLogger(DBConnection.class.getName());

    private static volatile DataSource dataSource;
    private static volatile boolean jndiLookupDone;

    public static Connection getPostgesConnection() throws SQLException {
        DataSource pool = dataSource();
        if (pool != null) {
            return pool.getConnection();
        }
        return DriverManager.getConnection(
                setting(ENV_URL, "boulangerie.db.url"),
                setting(ENV_USER, "boulangerie.db.user"),
                setting(ENV_PASSWORD, "boulangerie.db.password"));
    }

    private static DataSource dataSource() {
        if (!jndiLookupDone) {
            synchronized (DBConnection.class) {
                if (!jndiLookupDone) {
                    try {
                        dataSource = (DataSource) new InitialContext().lookup(JNDI_NAME);
                        LOG.info("Pool de connexions JNDI " + JNDI_NAME + " utilisé");
                    } catch (NamingException | ClassCastException e) {
                        dataSource = null;
                        LOG.info("Pas de DataSource JNDI (" + e.getMessage()
                                + ") : connexions directes via DriverManager");
                    }
                    jndiLookupDone = true;
                }
            }
        }
        return dataSource;
    }

    private static String setting(String envName, String propertyName) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            value = System.getenv(envName);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Configuration manquante : définir la propriété système "
                    + propertyName + " ou la variable d'environnement " + envName);
        }
        return value;
    }

}
