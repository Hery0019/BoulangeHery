package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import dao.User;

public class SessionUtils {

    public static boolean isUserConnected(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        Object user = session.getAttribute("user");
        return user != null;
    }

    public static User getConnectedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (User)session.getAttribute("user");
    }
    
}
