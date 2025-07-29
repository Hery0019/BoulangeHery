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
import dao.EntityInUseException;
import dao.Ingredient;
import dao.Perfume;
import dao.Recipe;
import util.Params;

public class RecipeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ArrayList<Category> categories = Category.all();
            ArrayList<Perfume> perfumes = Perfume.all();
            ArrayList<Ingredient> ingredients = Ingredient.all();

            String title = Params.string(req, "searchTitle", "");
            String description = Params.string(req, "searchDescription", "");
            int idCategory = Params.intValue(req, "searchIdCategory", 0);
            int idPerfume = Params.intValue(req, "searchIdPerfume", 0);
            String[] selectedIdsIngredient = req.getParameterValues("idIngredients");
            LocalTime minCookTime = Params.time(req, "searchMinCookTime");
            LocalTime maxCookTime = Params.time(req, "searchMaxCookTime");
            String creator = Params.string(req, "searchCreator", "");
            LocalDate minCreationDate = Params.date(req, "searchMinCreationDate");
            LocalDate maxCreationDate = Params.date(req, "searchMaxCreationDate");
            // 0 = critère non renseigné (même convention que Recipe.search)
            double minPrice = Params.doubleValue(req, "searchMinPrice", 0.0);
            double maxPrice = Params.doubleValue(req, "searchMaxPrice", 0.0);

            ArrayList<Recipe> recipes = Recipe.search(title, description, idCategory, idPerfume, minCookTime,
                    maxCookTime, creator, minCreationDate, maxCreationDate, selectedIdsIngredient, minPrice, maxPrice);
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

        if ("delete".equals(action)) {
            Recipe recipe = new Recipe(Params.requiredInt(req, "id"));
            try {
                recipe.delete();
            } catch (EntityInUseException e) {
                req.setAttribute("errorMessage", e.getMessage());
                doGet(req, resp);
                return;
            } catch (Exception e) {
                throw new ServletException(e);
            }
            resp.sendRedirect("recipe");
            return;
        }

        int id = Params.intValue(req, "idRecipe", 0);
        String title = Params.requiredString(req, "recipeTitle");
        String description = Params.string(req, "recipeDescription", "");
        int idCategory = Params.requiredInt(req, "recipeIdCategory");
        int idPerfume = Params.requiredInt(req, "recipeIdPerfume");
        LocalTime cookTime = Params.requiredTime(req, "recipeCookTime");
        String createdBy = Params.requiredString(req, "recipeCreator");
        double price = Params.requiredDouble(req, "recipePrice");
        LocalDate createdDate = Params.requiredDate(req, "recipeCreationDate");
        Recipe recipe = new Recipe(id, title, description, idCategory, idPerfume, cookTime, createdBy, createdDate,
                price);

        try {
            if ("update".equals(action)) {
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
