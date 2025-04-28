package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Recipe;
import dao.RecipePriceHistory;
import util.Params;

public class RecipePriceHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ArrayList<Recipe> recipies = Recipe.all();

            int idRecipe = Params.intValue(req, "searchIdRecipe", 0);
            LocalDate minChangeDate = Params.date(req, "searchMinChangeDate");
            LocalDate maxChangeDate = Params.date(req, "searchMaxChangeDate");

            ArrayList<RecipePriceHistory> recipePriceHistories = RecipePriceHistory.search(idRecipe, minChangeDate,
                    maxChangeDate, 0.0, 0.0, 0.0, 0.0);

            req.setAttribute("recipePriceHistories", recipePriceHistories);
            req.setAttribute("recipies", recipies);
            req.setAttribute("activeMenuItem", "recipe-price-history");
            req.setAttribute("pageTitle", "Historique des prix");

            RequestDispatcher dispatcher = req.getRequestDispatcher("recipe-price-history.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

}
