package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.User;
import util.Params;
import util.SessionUtils;

public class LoginServlet extends HttpServlet {

    /** GET /login n'a aucun effet : la déconnexion se fait en POST (action=logout). */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("form-login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("logout".equals(req.getParameter("action"))) {
            // Déconnexion : la session est détruite, pas seulement vidée de l'utilisateur.
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect("recipe");
            return;
        }

        String email = Params.requiredString(req, "userEmail");
        String password = Params.requiredString(req, "userPassword");
        try {
            User user = User.authenticate(email, password);
            if (user != null) {
                // Nouvel identifiant de session après authentification (anti-fixation de session)
                req.changeSessionId();
                req.getSession().setAttribute(SessionUtils.USER_ATTRIBUTE, user);
                resp.sendRedirect("recipe");
            } else {
                resp.sendRedirect("form-login?error=true");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

}
