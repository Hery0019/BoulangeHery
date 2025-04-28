package servlet;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Recipe;
import dao.RecipeIngredient;
import dao.Step;
import util.Params;

public class RecipeDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int idRecipe = Params.requiredInt(req, "idRecipe");
            LocalTime minCookTime = Params.time(req, "searchMinCookTime");
            LocalTime maxCookTime = Params.time(req, "searchMaxCookTime");

            Recipe recipe = Recipe.findById(idRecipe);
            if (recipe == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Recette introuvable");
                return;
            }
            ArrayList<Step> steps = Step.search(idRecipe, 0, 0, minCookTime, maxCookTime, "");
            ArrayList<RecipeIngredient> recipeIngredients = RecipeIngredient.search(idRecipe);

            req.setAttribute("recipe", recipe);
            req.setAttribute("steps", steps);
            req.setAttribute("recipeIngredients", recipeIngredients);
            req.setAttribute("activeMenuItem", "recipe");
            req.setAttribute("pageTitle", "Recette");

            RequestDispatcher dispatcher = req.getRequestDispatcher("recipe-details.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

}
