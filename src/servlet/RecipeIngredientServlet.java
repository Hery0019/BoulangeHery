package servlet;

import dao.Category;
import dao.Recipe;
import dao.RecipeIngredient;
import dao.Step;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class RecipeIngredientServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");
            int idRecipe = Integer.parseInt(req.getParameter("idRecipe"));

            if (action != null && action.equals("delete")) {
                int idIngredient = Integer.parseInt(req.getParameter("idIngredient"));
                RecipeIngredient recipeIngredient = new RecipeIngredient(idRecipe, idIngredient);
                recipeIngredient.delete();
            }

            resp.sendRedirect("recipe-details?idRecipe=" + idRecipe);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        int idRecipe = Integer.parseInt(req.getParameter("idRecipe"));
        int idIngredient = Integer.parseInt(req.getParameter("idIngredient"));
        double quantity = Double.parseDouble(req.getParameter("quantity"));

        RecipeIngredient recipeIngredient = new RecipeIngredient(idRecipe, idIngredient, quantity);

        try {
            if ("update".equals(action)) {
                recipeIngredient.update();
            } else if (recipeIngredient.find()) {
                RequestDispatcher dispatcher = req.getRequestDispatcher("form-recipe-ingredient?idRecipe=" + idRecipe +"&idIngredient=" + idIngredient);
                req.setAttribute("errorMessage", "La recette contient déjà cette ingrédient");
                dispatcher.forward(req, resp);
                return;
            } else {
                recipeIngredient.create();
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        resp.sendRedirect("recipe-details?idRecipe=" + idRecipe);
    }
}
