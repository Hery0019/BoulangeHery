package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Ingredient;
import util.Params;
import util.SessionUtils;

public class FormIngredientServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtils.isUserConnected(req)) {
            resp.sendRedirect("form-login");
            return;
        }

        String action = "update".equals(req.getParameter("action")) ? "update" : "create";
        // Valeurs re-soumises par IngredientServlet après un refus (doublon), le cas échéant
        Ingredient ingredient = (Ingredient) req.getAttribute("submitted");
        if (ingredient == null) {
            ingredient = new Ingredient();
            if ("update".equals(action)) {
                ingredient.setId(Params.requiredInt(req, "id"));
                try {
                    ingredient.find();
                } catch (Exception e) {
                    throw new ServletException(e);
                }
            }
        }

        req.setAttribute("action", action);
        req.setAttribute("ingredient", ingredient);
        req.setAttribute("activeMenuItem", "ingredient");
        req.setAttribute("pageTitle", "Ingrédient");

        RequestDispatcher dispatcher = req.getRequestDispatcher("form-ingredient.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Atteint uniquement par forward depuis IngredientServlet.doPost (refus)
        doGet(req, resp);
    }

}
