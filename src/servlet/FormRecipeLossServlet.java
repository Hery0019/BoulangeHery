package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Recipe;
import dao.RecipeLoss;
import util.Params;

/** Formulaire de constat de perte (création seulement). */
public class FormRecipeLossServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            RecipeLoss loss = (RecipeLoss) req.getAttribute("submitted");
            if (loss == null) {
                loss = new RecipeLoss();
                loss.setIdRecipe(Params.intValue(req, "idRecipe", 0));
            }
            req.setAttribute("loss", loss);
            req.setAttribute("recipies", Recipe.all());
            req.setAttribute("activeMenuItem", "recipe-loss");
            req.setAttribute("pageTitle", "Pertes");

            RequestDispatcher dispatcher = req.getRequestDispatcher("form-recipe-loss.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Atteint uniquement par forward depuis RecipeLossServlet.doPost (refus)
        doGet(req, resp);
    }
}
