package servlet;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Category;
import dao.Perfume;
import dao.Recipe;
import java.lang.reflect.Array;
import util.SessionUtils;

public class FormRecipeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtils.isUserConnected(req)) {
            resp.sendRedirect("form-login");
            return;
        }

        String action = req.getParameter("action");
        Recipe recipe = new Recipe();
        ArrayList<Category> categories;
        ArrayList<Perfume> perfumes;

        try {
            categories = Category.all();
            perfumes = Perfume.all();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        if (action != null && action.equals("update")) {
            int id = Integer.parseInt(req.getParameter("id"));
            recipe.setId(id);
            try {
                recipe.find();
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else {
            action = "create";
        }

        req.setAttribute("action", action);
        req.setAttribute("recipe", recipe);
        req.setAttribute("categories", categories);
        req.setAttribute("perfumes", perfumes);
        req.setAttribute("activeMenuItem", "recipe");
        req.setAttribute("pageTitle", "Recette");

        RequestDispatcher dispatcher = req.getRequestDispatcher("form-recipe.jsp");
        dispatcher.forward(req, resp);
    }

}