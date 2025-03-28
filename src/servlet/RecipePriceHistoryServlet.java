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

import dao.Recipe;
import dao.RecipePriceHistory;


public class RecipePriceHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");

            ArrayList<Recipe> recipies = Recipe.all();
            ArrayList<RecipePriceHistory> recipePriceHistories;

            int idRecipe = req.getParameter("searchIdRecipe") == null ? 0
            : Integer.parseInt(req.getParameter("searchIdRecipe"));

            String minChangeDateStr = req.getParameter("searchMinChangeDate");
            String maxChangeDateStr = req.getParameter("searchMaxChangeDate");
            LocalDate minChangeDate = null;
            LocalDate maxChangeDate = null;

            if (minChangeDateStr != null && !minChangeDateStr.equals("")) {
                minChangeDate = LocalDate.parse(minChangeDateStr);
            }

            if (maxChangeDateStr != null && !maxChangeDateStr.equals("")) {
                maxChangeDate = LocalDate.parse(maxChangeDateStr);
            }

            recipePriceHistories = RecipePriceHistory.search(idRecipe, minChangeDate, maxChangeDate, 0.0, 0.0, 0.0, 0.0);
            
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    }

}