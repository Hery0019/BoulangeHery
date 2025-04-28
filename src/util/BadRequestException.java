package util;

/**
 * Requête HTTP invalide (paramètre manquant ou mal formé).
 * Convertie en réponse 400 par {@code servlet.ErrorFilter}.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
