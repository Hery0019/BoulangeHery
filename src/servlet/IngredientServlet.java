package servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.DuplicateEntityException;
import dao.EntityInUseException;
import dao.Ingredient;
import util.Params;

public class IngredientServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String name = Params.string(req, "searchName", "");
            String unit = Params.string(req, "searchUnit", "");
            BigDecimal minPrice = Params.decimal(req, "searchMinPrice", null);
            BigDecimal maxPrice = Params.decimal(req, "searchMaxPrice", null);

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

        if ("delete".equals(action)) {
            // La clé étrangère recipe_ingredient -> ingredient est RESTRICT : un ingrédient
            // encore utilisé par une recette est refusé avec un message.
            Ingredient ingredient = new Ingredient(Params.requiredInt(req, "id"));
            try {
                ingredient.delete();
            } catch (EntityInUseException e) {
                req.setAttribute("errorMessage", e.getMessage());
                doGet(req, resp);
                return;
            } catch (Exception e) {
                throw new ServletException(e);
            }
            resp.sendRedirect("ingredient");
            return;
        }

        boolean update = "update".equals(action);
        int id = Params.intValue(req, "idIngredient", 0);
        String name = Params.requiredString(req, "ingredientName");
        String unit = Params.requiredString(req, "ingredientUnit");
        BigDecimal price = Params.requiredDecimal(req, "ingredientPrice");
        Ingredient ingredient = new Ingredient(id, name, unit, price);

        try {
            if (update) {
                ingredient.update();
            } else {
                ingredient.create();
            }
        } catch (DuplicateEntityException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("submitted", ingredient);
            req.getRequestDispatcher(update ? "form-ingredient?action=update&id=" + id : "form-ingredient")
                    .forward(req, resp);
            return;
        } catch (Exception e) {
            throw new ServletException(e);
        }

        resp.sendRedirect("ingredient");
    }

}
