package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import dao.User;

public class SessionUtils {

    public static final String USER_ATTRIBUTE = "user";

    public static boolean isUserConnected(HttpServletRequest req) {
        return getConnectedUser(req) != null;
    }

    /** Vrai si l'utilisateur connecté porte l'un des rôles donnés. */
    public static boolean hasAnyRole(HttpServletRequest req, String... roles) {
        User user = getConnectedUser(req);
        if (user == null) {
            return false;
        }
        for (String role : roles) {
            if (role.equals(user.getRole())) {
                return true;
            }
        }
        return false;
    }

    /** Recettes, étapes, ingrédients, catégories, stock. */
    public static boolean canManageCatalog(HttpServletRequest req) {
        return hasAnyRole(req, User.ADMIN, User.BOULANGER);
    }

    /** Ventes et commissions. */
    public static boolean canSell(HttpServletRequest req) {
        return hasAnyRole(req, User.ADMIN, User.VENDEUR);
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
