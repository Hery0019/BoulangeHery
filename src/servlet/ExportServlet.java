package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Commission;
import dao.RecipeSell;
import util.BadRequestException;
import util.Csv;
import util.Params;

/**
 * Export CSV des ventes et des commissions, avec les mêmes critères que les
 * pages correspondantes : le bouton d'export reprend la recherche affichée.
 */
public class ExportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = Params.string(req, "type", "");
        try {
            switch (type) {
                case "sales":
                    write(resp, "ventes", sales(req));
                    break;
                case "commissions":
                    write(resp, "commissions", commissions(req));
                    break;
                default:
                    throw new BadRequestException("Export inconnu : précisez type=sales ou type=commissions");
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private StringBuilder sales(HttpServletRequest req) throws Exception {
        StringBuilder csv = new StringBuilder(Csv.line(
                "Numéro", "Date", "Client", "Recette", "Catégorie", "Vendeur", "Saisi par",
                "Quantité", "Argent reçu", "Reste rendu"));
        for (RecipeSell sell : RecipeSell.search(
                Params.intValue(req, "searchIdRecipe", 0),
                Params.intValue(req, "searchIdCategory", 0),
                Params.intValue(req, "searchIdUser", 0),
                Params.intValue(req, "searchIdClient", 0),
                Params.intValue(req, "searchMinCombien", 0),
                Params.intValue(req, "searchMaxCombien", 0),
                Params.doubleValue(req, "searchMinArgent", 0.0),
                Params.doubleValue(req, "searchMaxArgent", 0.0),
                Params.doubleValue(req, "searchMinReste", 0.0),
                Params.doubleValue(req, "searchMaxReste", 0.0),
                Params.date(req, "searchMinSellDate"),
                Params.date(req, "searchMaxSellDate"))) {
            csv.append(Csv.line(
                    sell.getId(), sell.getFormattedCreatedDate(), sell.getClientLabel(), sell.getRecipeTitle(),
                    sell.getCategoryName(), sell.getVendeurName(), sell.getUserName(), sell.getCombien(),
                    Csv.amount(sell.getArgent()), Csv.amount(sell.getReste())));
        }
        return csv;
    }

    private StringBuilder commissions(HttpServletRequest req) throws Exception {
        StringBuilder csv = new StringBuilder(Csv.line(
                "Numéro", "Date", "Vendeur", "Sexe", "Recette", "Commission"));
        double total = 0.0;
        LocalDate minDate = Params.date(req, "searchMinCommissionDate");
        LocalDate maxDate = Params.date(req, "searchMaxCommissionDate");
        for (Commission commission : Commission.search(
                Params.intValue(req, "searchIdVendeur", 0),
                Params.intValue(req, "searchIdRecipe", 0),
                minDate, maxDate,
                Params.doubleValue(req, "searchCommissionAmount", 0.0),
                Params.string(req, "searchVendeurSexe", ""))) {
            total += commission.getCommissionsAmount();
            csv.append(Csv.line(
                    commission.getId(), commission.getFormattedcommissionDate(), commission.getVendeurName(),
                    commission.getVendeurSexe(), commission.getRecipeTitle(),
                    Csv.amount(commission.getCommissionsAmount())));
        }
        // Le total figure dans le fichier comme il figure au bas de la page.
        csv.append(Csv.line("", "", "", "", "Total", Csv.amount(total)));
        return csv;
    }

    private void write(HttpServletResponse resp, String name, StringBuilder csv) throws IOException {
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Disposition",
                "attachment; filename=\"" + name + "-" + LocalDate.now() + ".csv\"");
        try (PrintWriter out = resp.getWriter()) {
            out.write(Csv.BOM);
            out.write(csv.toString());
        }
    }
}
