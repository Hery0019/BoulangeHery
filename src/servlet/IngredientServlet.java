package servlet;

import java.io.IOException;
import java.util.ArrayList;

import dao.RecipeIngredient;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Ingredient;

public class IngredientServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");

            if ("delete".equals(action)) {
                int idIngredient = Integer.parseInt(req.getParameter("id"));
                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setIdIngredient(idIngredient);

                if (recipeIngredient.findByIdIngredient()) {
                    req.setAttribute("errorMessage", "Cet ingrédient est encore associé à une ou plusieurs recette(s)");
                } else {
                    Ingredient ingredient = new Ingredient(idIngredient);
                    ingredient.delete();
                }
            }

            String name = req.getParameter("searchName") == null ? "" : req.getParameter("searchName");
            String unit = req.getParameter("searchUnit") == null ? "" : req.getParameter("searchUnit");
            String minPriceStr = req.getParameter("searchMinPrice");
            String maxPriceStr = req.getParameter("searchMaxPrice");
            int minPrice = 0;
            int maxPrice = 0;

            if (minPriceStr != null && !minPriceStr.equals("")) {
                minPrice = Integer.parseInt(minPriceStr);
            }

            if (maxPriceStr != null && !maxPriceStr.equals("")) {
                maxPrice = Integer.parseInt(maxPriceStr);
            }

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
        int id = Integer.parseInt(req.getParameter("idIngredient"));
        String name = req.getParameter("ingredientName");
        String unit = req.getParameter("ingredientUnit");
        int price = Integer.parseInt(req.getParameter("ingredientPrice"));
        Ingredient ingredient = new Ingredient(id, name, unit, price);
        
        try {
            if (action != null && action.equals("update")) {
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
