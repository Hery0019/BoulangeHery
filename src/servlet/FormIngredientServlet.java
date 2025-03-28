package servlet;

import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Ingredient;
import util.SessionUtils;

public class FormIngredientServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtils.isUserConnected(req)) {
            resp.sendRedirect("form-login");
            return;
        }

        String action = req.getParameter("action");
        Ingredient ingredient = new Ingredient();

        if (action != null && action.equals("update")) {
            int id = Integer.parseInt(req.getParameter("id"));
            ingredient.setId(id);
            try {
                ingredient.find();
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else {
            action = "create";
        }

        req.setAttribute("action", action);
        req.setAttribute("ingredient", ingredient);
        req.setAttribute("activeMenuItem", "ingredient");
        req.setAttribute("pageTitle", "Ingrédient");

        RequestDispatcher dispatcher = req.getRequestDispatcher("form-ingredient.jsp");
        dispatcher.forward(req, resp);
    }

}