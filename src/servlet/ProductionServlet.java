package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.BusinessRuleException;
import dao.Production;
import dao.Recipe;
import dao.User;
import util.Params;
import util.SessionUtils;

/**
 * Ordres de fabrication : liste filtrable et enregistrement.
 *
 * Pas de modification ni de suppression : la production a réellement consommé
 * des matières premières, elle se corrige par une nouvelle écriture, pas en
 * réécrivant l'historique.
 */
public class ProductionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int idRecipe = Params.intValue(req, "searchIdRecipe", 0);
            LocalDate minDate = Params.date(req, "searchMinDate");
            LocalDate maxDate = Params.date(req, "searchMaxDate");

            ArrayList<Production> productions = Production.search(idRecipe, minDate, maxDate);
            req.setAttribute("productions", productions);
            req.setAttribute("recipies", Recipe.all());
            req.setAttribute("activeMenuItem", "production");
            req.setAttribute("pageTitle", "Production");

            RequestDispatcher dispatcher = req.getRequestDispatcher("production.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int idRecipe = Params.requiredInt(req, "productionIdRecipe");
        int quantity = Params.requiredInt(req, "productionQuantity");
        LocalDate date = Params.requiredDate(req, "productionDate");
        User author = SessionUtils.getConnectedUser(req);

        Production production = new Production(idRecipe, author == null ? 0 : author.getId(), quantity, date);
        try {
            production.create();
        } catch (Exception e) {
            BusinessRuleException rule = BusinessRuleException.from(e);
            if (rule == null) {
                throw new ServletException(e);
            }
            // Matière première insuffisante, quantité nulle... : on ré-affiche la saisie.
            req.setAttribute("errorMessage", rule.getMessage());
            req.setAttribute("submitted", production);
            req.getRequestDispatcher("form-production").forward(req, resp);
            return;
        }
        resp.sendRedirect("production");
    }
}
