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
import dao.Category;
import dao.Recipe;
import dao.RecipeSell;
import dao.User;
import dao.Vendeur;
import util.BadRequestException;
import util.Params;

public class RecipeSellServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");

            if ("delete".equals(action)) {
                RecipeSell recipeSell = new RecipeSell(Params.requiredInt(req, "id"));
                recipeSell.delete();
            }

            ArrayList<Recipe> recipies = Recipe.all();
            ArrayList<Category> categories = Category.all();
            ArrayList<User> users = User.all();
            ArrayList<Vendeur> vendeurs = Vendeur.all();

            int idRecipe = Params.intValue(req, "searchIdRecipe", 0);
            int idCategory = Params.intValue(req, "searchIdCategory", 0);
            int idUser = Params.intValue(req, "searchIdUser", 0);
            int minCombien = Params.intValue(req, "searchMinCombien", 0);
            int maxCombien = Params.intValue(req, "searchMaxCombien", 0);
            double minArgent = Params.doubleValue(req, "searchMinArgent", 0.0);
            double maxArgent = Params.doubleValue(req, "searchMaxArgent", 0.0);
            double minReste = Params.doubleValue(req, "searchMinReste", 0.0);
            double maxReste = Params.doubleValue(req, "searchMaxReste", 0.0);
            LocalDate minSellDate = Params.date(req, "searchMinSellDate");
            LocalDate maxSellDate = Params.date(req, "searchMaxSellDate");

            ArrayList<RecipeSell> recipeSells = RecipeSell.search(idRecipe, idCategory, idUser, minCombien,
                    maxCombien, minArgent, maxArgent, minReste, maxReste, minSellDate, maxSellDate);

            req.setAttribute("recipeSells", recipeSells);
            req.setAttribute("categories", categories);
            req.setAttribute("recipies", recipies);
            req.setAttribute("vendeurs", vendeurs);
            req.setAttribute("users", users);
            req.setAttribute("activeMenuItem", "recipe-sell");
            req.setAttribute("pageTitle", "Vente de recette");

            RequestDispatcher dispatcher = req.getRequestDispatcher("recipe-sell.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        boolean update = "update".equals(req.getParameter("action"));
        int id = Params.intValue(req, "idRecipeSell", 0);
        int idRecipe = Params.requiredInt(req, "idRecipe");
        int idVendeur = Params.requiredInt(req, "recipeSellerId");
        int idUser = Params.requiredInt(req, "recipeSellIdUser");
        int combien = Params.requiredInt(req, "recipeSellCombien");
        double argent = Params.requiredDouble(req, "recipeSellArgent");
        LocalDate sellDate = Params.requiredDate(req, "recipeSellDate");

        int idCategory;
        try {
            Recipe recipe = Recipe.findById(idRecipe);
            if (recipe == null) {
                throw new BadRequestException("Recette inconnue : " + idRecipe);
            }
            idCategory = recipe.getIdCategory();
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }

        // reste est calculé par le trigger calculate_reste
        RecipeSell recipeSell = new RecipeSell(id, idVendeur, idRecipe, idCategory, idUser, combien, argent, 0.0,
                sellDate);

        try {
            if (update) {
                recipeSell.update();
            } else {
                recipeSell.create();
            }
        } catch (Exception e) {
            BusinessRuleException rule = BusinessRuleException.from(e);
            if (rule == null) {
                throw new ServletException(e);
            }
            // Règle métier refusée (stock, montant...) : on ré-affiche le formulaire
            // avec les valeurs saisies et le message.
            req.setAttribute("errorMessage", rule.getMessage());
            req.setAttribute("submitted", recipeSell);
            String target = update ? "form-recipe-sell?action=update&id=" + id : "form-recipe-sell";
            req.getRequestDispatcher(target).forward(req, resp);
            return;
        }

        resp.sendRedirect("recipe-sell");
    }

}
