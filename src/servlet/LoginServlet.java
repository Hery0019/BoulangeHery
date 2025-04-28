package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.User;
import util.Params;
import util.SessionUtils;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().removeAttribute(SessionUtils.USER_ATTRIBUTE);
        resp.sendRedirect("recipe");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = Params.requiredString(req, "userEmail");
        String password = Params.requiredString(req, "userPassword");
        try {
            User user = User.authenticate(email, password);
            if (user != null) {
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
