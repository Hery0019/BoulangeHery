package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Jeton anti-CSRF (motif « synchronizer token ») : un jeton aléatoire par
 * session, placé dans chaque formulaire POST ({@link #PARAMETER}) et vérifié
 * par {@code servlet.CsrfFilter}.
 */
public final class Csrf {

    public static final String PARAMETER = "_csrf";
    private static final String SESSION_ATTRIBUTE = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private Csrf() {
    }

    /** Jeton de la session courante, créé au premier appel (la session aussi si nécessaire). */
    public static String token(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        synchronized (session) {
            String token = (String) session.getAttribute(SESSION_ATTRIBUTE);
            if (token == null) {
                byte[] bytes = new byte[32];
                RANDOM.nextBytes(bytes);
                token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                session.setAttribute(SESSION_ATTRIBUTE, token);
            }
            return token;
        }
    }

    /** Vrai si la requête porte le jeton de sa session (comparaison en temps constant). */
    public static boolean isValid(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        }
        String expected = (String) session.getAttribute(SESSION_ATTRIBUTE);
        String actual = req.getParameter(PARAMETER);
        if (expected == null || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }
}
