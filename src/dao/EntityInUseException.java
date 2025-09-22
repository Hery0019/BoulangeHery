package dao;

import java.sql.SQLException;

/**
 * Levée quand une suppression est refusée parce que d'autres données
 * référencent encore l'entité : violation de clé étrangère (SQLSTATE 23503) ou
 * refus d'une clé ON DELETE RESTRICT (SQLSTATE 23001 depuis PostgreSQL 18).
 * Le message est destiné à l'utilisateur.
 */
public class EntityInUseException extends Exception {

    private static final String FOREIGN_KEY_VIOLATION = "23503";
    // PostgreSQL 18 distingue le refus d'une clé RESTRICT du 23503 générique.
    private static final String RESTRICT_VIOLATION = "23001";

    public EntityInUseException(String message) {
        super(message);
    }

    public static boolean isForeignKeyViolation(Throwable t) {
        return t instanceof SQLException
                && (FOREIGN_KEY_VIOLATION.equals(((SQLException) t).getSQLState())
                        || RESTRICT_VIOLATION.equals(((SQLException) t).getSQLState()));
    }
}
