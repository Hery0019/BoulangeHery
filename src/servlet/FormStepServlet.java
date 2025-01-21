package servlet;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Recipe;
import dao.Step;
import util.SessionUtils;

public class FormStepServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtils.isUserConnected(req)) {
            resp.sendRedirect("form-login");
            return;
        }

        String action = req.getParameter("action");
        ArrayList<Recipe> recipes;
        int idRecipe = req.getParameter("idRecipe") == null ? 0 : Integer.parseInt(req.getParameter("idRecipe"));
        Step step = new Step();
        step.setIdRecipe(idRecipe);

        try {
            recipes = Recipe.all();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        if (action != null && action.equals("update")) {
            int id = Integer.parseInt(req.getParameter("id"));
            step.setId(id);
            try {
                step.find();
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else {
            action = "create";
        }

        req.setAttribute("action", action);
        req.setAttribute("step", step);
        req.setAttribute("recipes", recipes);
        req.setAttribute("activeMenuItem", "step");
        req.setAttribute("pageTitle", "Etape");

        RequestDispatcher dispatcher = req.getRequestDispatcher("form-step.jsp");
        dispatcher.forward(req, resp);
    }

}