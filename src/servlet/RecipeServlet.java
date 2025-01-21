package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Category;
import dao.Recipe;
import dao.Ingredient;
import dao.Perfume;

public class RecipeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");

            if (action != null && action.equals("delete")) {
                int id = Integer.parseInt(req.getParameter("id"));
                Recipe recipe = new Recipe(id);
                recipe.delete();
            }

            ArrayList<Category> categories = Category.all();
            ArrayList<Perfume> perfumes = Perfume.all();
            ArrayList<Ingredient> ingredients = Ingredient.all();

            String title = req.getParameter("searchTitle") == null ? "" : req.getParameter("searchTitle");
            String description = req.getParameter("searchDescription") == null ? ""
                    : req.getParameter("searchDescription");
            int idCategory = req.getParameter("searchIdCategory") == null ? 0
                    : Integer.parseInt(req.getParameter("searchIdCategory"));

            int idPerfume = req.getParameter("searchIdPerfume") == null ? 0
                    : Integer.parseInt(req.getParameter("searchIdPerfume"));


            String[] selectedIdsIngredient = req.getParameterValues("idIngredients");

            String minCookTimeStr = req.getParameter("searchMinCookTime");
            String maxCookTimeStr = req.getParameter("searchMaxCookTime");
            LocalTime minCookTime = null;
            LocalTime maxCookTime = null;
            String creator = req.getParameter("searchCreator") == null ? "" : req.getParameter("searchCreator");
            String minCreationDateStr = req.getParameter("searchMinCreationDate");
            String maxCreationDateStr = req.getParameter("searchMaxCreationDate");
            LocalDate minCreationDate = null;
            LocalDate maxCreationDate = null;

            
            String minPriceStr = req.getParameter("searchMinPrice") == null ? "1"
            : req.getParameter("searchMinPrice");

            String maxPriceStr = req.getParameter("searchMaxPrice") == null ? "100000000"
            : req.getParameter("searchMaxPrice");
            

            double minPrice = Double.parseDouble(minPriceStr);
            double maxPrice = Double.parseDouble(maxPriceStr);

            if (minCookTimeStr != null && !minCookTimeStr.equals("")) {
                minCookTime = LocalTime.parse(minCookTimeStr);
            }

            if (maxCookTimeStr != null && !maxCookTimeStr.equals("")) {
                maxCookTime = LocalTime.parse(maxCookTimeStr);
            }

            if (minCreationDateStr != null && !minCreationDateStr.equals("")) {
                minCreationDate = LocalDate.parse(minCreationDateStr);
            }

            if (maxCreationDateStr != null && !maxCreationDateStr.equals("")) {
                maxCreationDate = LocalDate.parse(maxCreationDateStr);
            }

            ArrayList<Recipe> recipes = Recipe.search(title, description, idCategory, idPerfume, minCookTime, maxCookTime, creator,
                    minCreationDate, maxCreationDate, selectedIdsIngredient, minPrice, maxPrice);
            req.setAttribute("recipes", recipes);
            req.setAttribute("categories", categories);
            req.setAttribute("ingredients", ingredients);
            req.setAttribute("perfumes", perfumes);
            req.setAttribute("activeMenuItem", "recipe");
            req.setAttribute("pageTitle", "Recette");

            RequestDispatcher dispatcher = req.getRequestDispatcher("recipe.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String action = req.getParameter("action");
        int id = Integer.parseInt(req.getParameter("idRecipe"));
        String title = req.getParameter("recipeTitle");
        String description = req.getParameter("recipeDescription");
        int idCategory = Integer.parseInt(req.getParameter("recipeIdCategory"));
        int idPerfume = Integer.parseInt(req.getParameter("recipeIdPerfume"));
        LocalTime cookTime = LocalTime.parse(req.getParameter("recipeCookTime"));
        String createdBy = req.getParameter("recipeCreator");
        double price = Double.parseDouble(req.getParameter("recipePrice"));
        LocalDate createdDate = LocalDate.parse(req.getParameter("recipeCreationDate"));
        Recipe recipe = new Recipe(id, title, description, idCategory, idPerfume, cookTime, createdBy, createdDate, price);

        try {
            if (action != null && action.equals("update")) {
                recipe.update();
            } else {
                recipe.create();
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        resp.sendRedirect("recipe");
    }

}