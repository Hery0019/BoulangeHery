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
import dao.Recipe;
import dao.RecipeLoss;
import dao.User;
import util.Params;
import util.SessionUtils;

/** Pertes de produits finis : liste filtrable et enregistrement. */
public class RecipeLossServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int idRecipe = Params.intValue(req, "searchIdRecipe", 0);
            String reason = Params.string(req, "searchReason", "");
            LocalDate minDate = Params.date(req, "searchMinDate");
            LocalDate maxDate = Params.date(req, "searchMaxDate");

            ArrayList<RecipeLoss> losses = RecipeLoss.search(idRecipe, reason, minDate, maxDate);
            int total = 0;
            for (RecipeLoss loss : losses) {
                total += loss.getQuantity();
            }
            req.setAttribute("losses", losses);
            req.setAttribute("totalLost", total);
            req.setAttribute("recipies", Recipe.all());
            req.setAttribute("activeMenuItem", "recipe-loss");
            req.setAttribute("pageTitle", "Pertes");

            RequestDispatcher dispatcher = req.getRequestDispatcher("recipe-loss.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int idRecipe = Params.requiredInt(req, "lossIdRecipe");
        int quantity = Params.requiredInt(req, "lossQuantity");
        String reason = Params.requiredString(req, "lossReason");
        LocalDate date = Params.requiredDate(req, "lossDate");
        User author = SessionUtils.getConnectedUser(req);

        RecipeLoss loss = new RecipeLoss(idRecipe, author == null ? 0 : author.getId(), quantity, reason, date);
        try {
            loss.create();
        } catch (Exception e) {
            BusinessRuleException rule = BusinessRuleException.from(e);
            if (rule == null) {
                throw new ServletException(e);
            }
            // Plus de pertes que de stock : on ré-affiche la saisie avec le motif du refus.
            req.setAttribute("errorMessage", rule.getMessage());
            req.setAttribute("submitted", loss);
            req.getRequestDispatcher("form-recipe-loss").forward(req, resp);
            return;
        }
        resp.sendRedirect("recipe-loss");
    }
}
