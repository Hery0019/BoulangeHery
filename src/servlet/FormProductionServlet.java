package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Production;
import dao.Recipe;
import util.Params;

/** Formulaire d'ordre de fabrication (création seulement). */
public class FormProductionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Valeurs re-soumises par ProductionServlet après un refus, le cas échéant
            Production production = (Production) req.getAttribute("submitted");
            if (production == null) {
                production = new Production();
                production.setIdRecipe(Params.intValue(req, "idRecipe", 0));
            }
            req.setAttribute("production", production);
            req.setAttribute("recipies", Recipe.all());
            req.setAttribute("activeMenuItem", "production");
            req.setAttribute("pageTitle", "Production");

            RequestDispatcher dispatcher = req.getRequestDispatcher("form-production.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Atteint uniquement par forward depuis ProductionServlet.doPost (refus)
        doGet(req, resp);
    }
}
