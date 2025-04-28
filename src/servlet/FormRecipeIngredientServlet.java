package servlet;

import dao.Ingredient;
import dao.Recipe;
import dao.RecipeIngredient;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.Params;
import util.SessionUtils;

import java.io.IOException;
import java.util.ArrayList;

public class FormRecipeIngredientServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtils.isUserConnected(req)) {
            resp.sendRedirect("form-login");
            return;
        }

        try {
            String action = req.getParameter("action");
            int idRecipe = Params.requiredInt(req, "idRecipe");
            int idIngredient = Params.intValue(req, "idIngredient", 0);

            RecipeIngredient recipeIngredient = new RecipeIngredient(idRecipe, idIngredient);
            recipeIngredient.find();
            Recipe recipe = new Recipe(idRecipe);
            recipe.find();
            ArrayList<Ingredient> ingredients = Ingredient.all();

            if (!"update".equals(action)) {
                action = "create";
            }

            req.setAttribute("action", action);
            req.setAttribute("recipeIngredient", recipeIngredient);
            req.setAttribute("recipe", recipe);
            req.setAttribute("ingredients", ingredients);
            req.setAttribute("activeMenuItem", "ingredient");
            req.setAttribute("pageTitle", "Ingrédient : " + recipe.getTitle());

            RequestDispatcher dispatcher = req.getRequestDispatcher("form-recipe-ingredient.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}