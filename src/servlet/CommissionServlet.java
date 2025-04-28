package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Commission;
import dao.Recipe;
import dao.Vendeur;
import util.Params;

public class CommissionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ArrayList<Vendeur> vendeurs = Vendeur.all();
            ArrayList<Recipe> recipies = Recipe.all();

            int idVendeur = Params.intValue(req, "searchIdVendeur", 0);
            int idRecipe = Params.intValue(req, "searchIdRecipe", 0);
            LocalDate minCommissionDate = Params.date(req, "searchMinCommissionDate");
            LocalDate maxCommissionDate = Params.date(req, "searchMaxCommissionDate");
            String sexeVendeur = Params.string(req, "searchVendeurSexe", "");
            double commissionAmount = Params.doubleValue(req, "searchCommissionAmount", 0.0);

            ArrayList<Commission> commissions = Commission.search(idVendeur, idRecipe,
                    minCommissionDate, maxCommissionDate, commissionAmount, sexeVendeur);
            req.setAttribute("recipies", recipies);
            req.setAttribute("vendeurs", vendeurs);
            req.setAttribute("commissions", commissions);
            req.setAttribute("activeMenuItem", "commission");
            req.setAttribute("pageTitle", "Liste Commission");

            RequestDispatcher dispatcher = req.getRequestDispatcher("commission.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

}
