package servlet;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.EntityInUseException;
import dao.Ingredient;
import dao.RecipeIngredient;
import util.Params;

public class IngredientServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");

            if ("delete".equals(action)) {
                int idIngredient = Params.requiredInt(req, "id");
                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setIdIngredient(idIngredient);

                if (recipeIngredient.findByIdIngredient()) {
                    req.setAttribute("errorMessage", "Cet ingrédient est encore associé à une ou plusieurs recette(s)");
                } else {
                    Ingredient ingredient = new Ingredient(idIngredient);
                    try {
                        ingredient.delete();
                    } catch (EntityInUseException e) {
                        req.setAttribute("errorMessage", e.getMessage());
                    }
                }
            }

            String name = Params.string(req, "searchName", "");
            String unit = Params.string(req, "searchUnit", "");
            int minPrice = Params.intValue(req, "searchMinPrice", 0);
            int maxPrice = Params.intValue(req, "searchMaxPrice", 0);

            ArrayList<Ingredient> ingredients = Ingredient.search(name, unit, minPrice, maxPrice);

            req.setAttribute("ingredients", ingredients);
            req.setAttribute("activeMenuItem", "ingredient");
            req.setAttribute("pageTitle", "Ingrédient");

            RequestDispatcher dispatcher = req.getRequestDispatcher("ingredient.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String action = req.getParameter("action");
        int id = Params.intValue(req, "idIngredient", 0);
        String name = Params.requiredString(req, "ingredientName");
        String unit = Params.requiredString(req, "ingredientUnit");
        int price = Params.requiredInt(req, "ingredientPrice");
        Ingredient ingredient = new Ingredient(id, name, unit, price);

        try {
            if ("update".equals(action)) {
                ingredient.update();
            } else {
                ingredient.create();
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        resp.sendRedirect("ingredient");
    }

}
