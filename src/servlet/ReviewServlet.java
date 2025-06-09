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
import util.Params;
import util.SessionUtils;

public class ReviewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");

            if ("delete".equals(action)) {
                Review review = new Review(Params.requiredInt(req, "id"));
                review.delete();
            }

            ArrayList<User> users = User.all();
            ArrayList<Recipe> recipes = Recipe.all();

            int idUser = Params.intValue(req, "searchIdUser", 0);
            int idRecipe = Params.intValue(req, "searchIdRecipe", 0);
            int minMark = Params.intValue(req, "searchMinMark", 1);
            int maxMark = Params.intValue(req, "searchMaxMark", 5);
            String comment = Params.string(req, "searchComment", "");
            LocalDate minDate = Params.date(req, "searchMinDate");
            LocalDate maxDate = Params.date(req, "searchMaxDate");

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
        int id = Params.intValue(req, "idReview", 0);
        // L'auteur est l'utilisateur connecté, jamais un champ du formulaire (ignoré à la modification)
        int idUser = SessionUtils.getConnectedUser(req).getId();
        int idRecipe = Params.requiredInt(req, "reviewIdRecipe");
        int rating = Params.requiredInt(req, "reviewRating");
        String comment = Params.string(req, "reviewComment", "");
        Review review = new Review(id, idUser, idRecipe, rating, comment);

        try {
            if ("update".equals(action)) {
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
