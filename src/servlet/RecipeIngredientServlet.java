package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.DuplicateEntityException;
import dao.RecipeIngredient;
import util.Params;

public class RecipeIngredientServlet extends HttpServlet {

    /** Pas de contenu propre : on renvoie vers la fiche de la recette. */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("recipe-details?idRecipe=" + Params.requiredInt(req, "idRecipe"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        int idRecipe = Params.requiredInt(req, "idRecipe");
        int idIngredient = Params.requiredInt(req, "idIngredient");

        if ("delete".equals(action)) {
            try {
                new RecipeIngredient(idRecipe, idIngredient).delete();
            } catch (Exception e) {
                throw new ServletException(e);
            }
            resp.sendRedirect("recipe-details?idRecipe=" + idRecipe);
            return;
        }

        double quantity = Params.requiredDouble(req, "quantity");
        RecipeIngredient recipeIngredient = new RecipeIngredient(idRecipe, idIngredient, quantity);

        try {
            if ("update".equals(action)) {
                recipeIngredient.update();
            } else {
                // La clé primaire (id_recipe, id_ingredient) garantit l'unicité, sans lecture préalable.
                recipeIngredient.create();
            }
        } catch (DuplicateEntityException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("form-recipe-ingredient?idRecipe=" + idRecipe + "&idIngredient=" + idIngredient)
                    .forward(req, resp);
            return;
        } catch (Exception e) {
            throw new ServletException(e);
        }

        resp.sendRedirect("recipe-details?idRecipe=" + idRecipe);
    }
}
