package servlet;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.RecipeStock;
import util.BadRequestException;
import util.Params;

/** Approvisionnement du stock d'une recette et seuil d'alerte (page de détails). */
public class RecipeStockServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int idRecipe = Params.requiredInt(req, "idRecipe");

        if ("threshold".equals(req.getParameter("action"))) {
            int seuil = Params.requiredInt(req, "seuilAlerte");
            if (seuil < 0) {
                throw new BadRequestException("Le seuil d'alerte ne peut pas être négatif");
            }
            try {
                RecipeStock.setThreshold(idRecipe, seuil);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            resp.sendRedirect("recipe-details?idRecipe=" + idRecipe);
            return;
        }

        int quantity = Params.requiredInt(req, "quantity");
        if (quantity <= 0) {
            throw new BadRequestException("La quantité à ajouter doit être supérieure à zéro");
        }
        try {
            RecipeStock.add(idRecipe, quantity);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        resp.sendRedirect("recipe-details?idRecipe=" + idRecipe);
    }

}
