package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Dashboard;
import util.SessionUtils;

/**
 * Tableau de bord.
 *
 * Le contenu suit les rôles : la caisse voit le chiffre d'affaires et les
 * commissions, la boulangerie voit la production, les stocks et les marges.
 * Un administrateur voit l'ensemble. Rien n'est calculé pour un rôle qui n'y a
 * pas accès — la page ne se contente pas de masquer.
 */
public class DashboardServlet extends HttpServlet {

    private static final int DAYS = 7;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean sales = SessionUtils.canSell(req);
        boolean catalog = SessionUtils.canManageCatalog(req);

        try {
            if (sales) {
                req.setAttribute("revenueToday", Dashboard.scalar(
                        "SELECT COALESCE(SUM(argent - reste), 0) FROM recipe_sell WHERE sell_date = CURRENT_DATE"));
                req.setAttribute("salesToday", Dashboard.scalar(
                        "SELECT count(*) FROM recipe_sell WHERE sell_date = CURRENT_DATE"));
                req.setAttribute("commissionsMonth", Dashboard.scalar(
                        "SELECT COALESCE(SUM(commission_amount), 0) FROM commission"
                                + " WHERE commission_date >= date_trunc('month', CURRENT_DATE)"));
                req.setAttribute("revenueByDay", Dashboard.revenueByDay(DAYS));
                req.setAttribute("topRecipes", Dashboard.topRecipes(5));
                req.setAttribute("revenueByCategory", Dashboard.revenueByCategory());
                req.setAttribute("commissionsBySeller", Dashboard.commissionsBySeller());
            }
            if (catalog) {
                req.setAttribute("producedToday", Dashboard.scalar(
                        "SELECT COALESCE(SUM(quantity), 0) FROM production WHERE production_date = CURRENT_DATE"));
                req.setAttribute("lostMonth", Dashboard.scalar(
                        "SELECT COALESCE(SUM(quantity), 0) FROM recipe_loss"
                                + " WHERE loss_date >= date_trunc('month', CURRENT_DATE)"));
                req.setAttribute("productionByDay", Dashboard.productionByDay(DAYS));
                req.setAttribute("lossesByReason", Dashboard.lossesByReason(30));
                req.setAttribute("thinnestMargins", Dashboard.thinnestMargins(5));
                req.setAttribute("lowStock", Dashboard.lowStock());
                req.setAttribute("lowIngredients", Dashboard.lowIngredients(5));
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        req.setAttribute("showSales", sales);
        req.setAttribute("showCatalog", catalog);
        req.setAttribute("days", DAYS);
        req.setAttribute("activeMenuItem", "dashboard");
        req.setAttribute("pageTitle", "Tableau de bord");

        RequestDispatcher dispatcher = req.getRequestDispatcher("dashboard.jsp");
        dispatcher.forward(req, resp);
    }
}
