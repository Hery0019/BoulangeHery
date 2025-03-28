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
import dao.Review;
import dao.User;

public class ReviewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");

            if (action != null && action.equals("delete")) {
                int id = Integer.parseInt(req.getParameter("id"));
                Review review = new Review(id);
                review.delete();
            }

            ArrayList<User> users = User.all();
            ArrayList<Recipe> recipes = Recipe.all();

            int idUser = req.getParameter("searchIdUser") == null ? 0 : Integer.parseInt(req.getParameter("searchIdUser"));
            int idRecipe = req.getParameter("searchIdRecipe") == null ? 0 : Integer.parseInt(req.getParameter("searchIdRecipe"));
            int minMark = req.getParameter("searchMinMark") == null ? 1 : Integer.parseInt(req.getParameter("searchMinMark"));
            int maxMark = req.getParameter("searchMaxMark") == null ? 5 : Integer.parseInt(req.getParameter("searchMaxMark"));
            String comment = req.getParameter("searchComment") == null ? "" : req.getParameter("searchComment");
            String minDateStr = req.getParameter("searchMinDate");
            String maxDateStr = req.getParameter("searchMaxDate");
            LocalDate minDate = null;
            LocalDate maxDate = null;

            if (minDateStr != null && !minDateStr.equals("")) {
                minDate = LocalDate.parse(minDateStr);
            }

            if (maxDateStr != null && !maxDateStr.equals("")) {
                maxDate = LocalDate.parse(maxDateStr);
            }

            ArrayList<Review> reviews = Review.search(idUser, idRecipe, minMark, maxMark, comment, minDate, maxDate);
            req.setAttribute("reviews", reviews);
            req.setAttribute("users", users);
            req.setAttribute("recipes", recipes);
            req.setAttribute("activeMenuItem", "review");
            req.setAttribute("pageTitle", "Nos conseils");

            RequestDispatcher dispatcher = req.getRequestDispatcher("review.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String action = req.getParameter("action");
        int id = Integer.parseInt(req.getParameter("idReview"));
        int idUser = Integer.parseInt(req.getParameter("reviewIdUser"));
        int idRecipe = Integer.parseInt(req.getParameter("reviewIdRecipe"));
        int rating = Integer.parseInt(req.getParameter("reviewRating"));
        String comment = req.getParameter("reviewComment");
        Review review = new Review(id, idUser, idRecipe, rating, comment);

        try {
            if (action != null && action.equals("update")) {
                review.update();
            } else {
                review.create();
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        resp.sendRedirect("review");
    }

}
