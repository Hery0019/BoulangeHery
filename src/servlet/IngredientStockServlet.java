package servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.IngredientStock;
import util.BadRequestException;
import util.Params;

/** Approvisionnement en matière première, depuis la liste des ingrédients. */
public class IngredientStockServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int idIngredient = Params.requiredInt(req, "idIngredient");
        BigDecimal quantity = Params.requiredDecimal(req, "quantity");
        if (quantity.signum() <= 0) {
            throw new BadRequestException("La quantité à ajouter doit être supérieure à zéro");
        }
        try {
            IngredientStock.add(idIngredient, quantity);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        resp.sendRedirect("ingredient");
    }
}
