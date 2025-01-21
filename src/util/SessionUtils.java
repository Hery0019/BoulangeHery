package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import dao.User;

public class SessionUtils {

    public static final String USER_ATTRIBUTE = "user";

    public static boolean isUserConnected(HttpServletRequest req) {
        return getConnectedUser(req) != null;
    }

    /** Renvoie l'utilisateur connecté, ou null s'il n'y a pas de session valide. */
    public static User getConnectedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        Object user = session.getAttribute(USER_ATTRIBUTE);
        return user instanceof User ? (User) user : null;
    }

}
