package servlet;

import dao.Recipe;
import dao.RecipeIngredient;
import dao.Step;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;

public class RecipeDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int idRecipe = req.getParameter("idRecipe") == null ? 0 : Integer.parseInt(req.getParameter("idRecipe"));
            String minCookTimeStr = req.getParameter("searchMinCookTime");
            String maxCookTimeStr = req.getParameter("searchMaxCookTime");
            LocalTime minCookTime = null;
            LocalTime maxCookTime = null;

            if (minCookTimeStr != null && !minCookTimeStr.equals("")) {
                minCookTime = LocalTime.parse(minCookTimeStr);
            }

            if (maxCookTimeStr != null && !maxCookTimeStr.equals("")) {
                maxCookTime = LocalTime.parse(maxCookTimeStr);
            }

            Recipe recipe = new Recipe(idRecipe);
            recipe.find();
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