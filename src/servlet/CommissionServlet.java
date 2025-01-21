package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Vendeur;
import dao.Recipe;
import dao.Commission;


public class CommissionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");

            ArrayList<Vendeur> vendeurs = Vendeur.all();
            ArrayList<Recipe> recipies = Recipe.all();
            

            int idVendeur = req.getParameter("searchIdVendeur") == null ? 0
                    : Integer.parseInt(req.getParameter("searchIdVendeur"));

            int idRecipe = req.getParameter("searchidRecipe") == null ? 0
                    : Integer.parseInt(req.getParameter("searchidRecipe"));
          
            String minCommissionDateStr = req.getParameter("searchMinCommissionDate");
            String maxCommissionDateStr = req.getParameter("searchMaxCommissionDate");
            LocalDate minCommissionDate = null;
            LocalDate maxCommissionDate = null;

            String sexeVendeur = req.getParameter("searchVendeurSexe") == null ? "" : req.getParameter("searchVendeurSexe");
            
            String commissionAmountStr = req.getParameter("searchCommissionAmount") == null ? "0"
            : req.getParameter("searchCommissionAmount");

            double commissionAmount = Double.parseDouble(commissionAmountStr);


            if (minCommissionDateStr != null && !minCommissionDateStr.equals("")) {
                minCommissionDate = LocalDate.parse(minCommissionDateStr);
            }

            if (maxCommissionDateStr != null && !maxCommissionDateStr.equals("")) {
                maxCommissionDate = LocalDate.parse(maxCommissionDateStr);
            }

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    }

}