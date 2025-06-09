package dao;

import java.sql.SQLException;

import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

/**
 * Règle métier refusée par la base : exception levée par un trigger
 * (SQLSTATE P0001, message rédigé pour l'utilisateur) ou contrainte CHECK
 * (SQLSTATE 23514, traduite ici par nom de contrainte).
 *
 * Les servlets appellent {@link #from(Throwable)} : si le résultat n'est pas
 * {@code null}, l'erreur est affichée à l'utilisateur ; sinon c'est une erreur
 * technique à journaliser.
 */
public class BusinessRuleException extends Exception {

    private static final String RAISE_EXCEPTION = "P0001";
    private static final String CHECK_VIOLATION = "23514";

    public BusinessRuleException(String message) {
        super(message);
    }

    /** Parcourt la chaîne des causes et renvoie l'erreur métier, ou {@code null}. */
    public static BusinessRuleException from(Throwable t) {
        for (Throwable cause = t; cause != null; cause = cause.getCause() == cause ? null : cause.getCause()) {
            if (cause instanceof SQLException) {
                BusinessRuleException rule = fromSql((SQLException) cause);
                if (rule != null) {
                    return rule;
                }
            }
        }
        return null;
    }

    private static BusinessRuleException fromSql(SQLException e) {
        String state = e.getSQLState();
        if (RAISE_EXCEPTION.equals(state)) {
            return new BusinessRuleException(primaryMessage(e));
        }
        if (CHECK_VIOLATION.equals(state)) {
            String constraint = constraintName(e);
            switch (constraint == null ? "" : constraint) {
                case "recipe_sell_reste_check":
                    return new BusinessRuleException(
                            "Montant insuffisant : l'argent reçu ne couvre pas le prix de la vente.");
                case "recipe_stock_reste_check":
                    return new BusinessRuleException("Stock insuffisant pour cette recette.");
                case "review_rating_check":
                    return new BusinessRuleException("La note doit être comprise entre 1 et 5.");
                default:
                    return new BusinessRuleException("Données refusées par la base (" + constraint + ").");
            }
        }
        return null;
    }

    private static String primaryMessage(SQLException e) {
        if (e instanceof PSQLException) {
            ServerErrorMessage server = ((PSQLException) e).getServerErrorMessage();
            if (server != null && server.getMessage() != null) {
                return server.getMessage();
            }
        }
        return e.getMessage();
    }

    private static String constraintName(SQLException e) {
        if (e instanceof PSQLException) {
            ServerErrorMessage server = ((PSQLException) e).getServerErrorMessage();
            if (server != null) {
                return server.getConstraint();
            }
        }
        return null;
    }
}
