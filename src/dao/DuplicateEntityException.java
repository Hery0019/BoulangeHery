package dao;

import java.sql.SQLException;

/**
 * Création ou modification refusée parce qu'une contrainte d'unicité est
 * violée (SQLSTATE 23505). Le message est destiné à l'utilisateur.
 */
public class DuplicateEntityException extends Exception {

    private static final String UNIQUE_VIOLATION = "23505";

    public DuplicateEntityException(String message) {
        super(message);
    }

    /**
     * Renvoie une {@code DuplicateEntityException} portant {@code message} si
     * {@code e} (ou l'une de ses causes) est une violation d'unicité, sinon
     * {@code e} inchangée — à utiliser dans un {@code throw}.
     */
    public static Exception orSame(Exception e, String message) {
        for (Throwable cause = e; cause != null; cause = cause.getCause() == cause ? null : cause.getCause()) {
            if (cause instanceof SQLException && UNIQUE_VIOLATION.equals(((SQLException) cause).getSQLState())) {
                return new DuplicateEntityException(message);
            }
        }
        return e;
    }
}
