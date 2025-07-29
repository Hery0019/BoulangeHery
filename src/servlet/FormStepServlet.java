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
import util.Params;
import util.SessionUtils;

public class FormStepServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtils.isUserConnected(req)) {
            resp.sendRedirect("form-login");
            return;
        }

        String action = "update".equals(req.getParameter("action")) ? "update" : "create";
        ArrayList<Recipe> recipes;
        // Valeurs re-soumises par StepServlet après un refus (doublon), le cas échéant
        Step step = (Step) req.getAttribute("submitted");

        try {
            recipes = Recipe.all();
            if (step == null) {
                step = new Step();
                step.setIdRecipe(Params.intValue(req, "idRecipe", 0));
                if ("update".equals(action)) {
                    step.setId(Params.requiredInt(req, "id"));
                    step.find();
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        req.setAttribute("action", action);
        req.setAttribute("step", step);
        req.setAttribute("recipes", recipes);
        req.setAttribute("activeMenuItem", "step");
        req.setAttribute("pageTitle", "Etape");

        RequestDispatcher dispatcher = req.getRequestDispatcher("form-step.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Atteint uniquement par forward depuis StepServlet.doPost (refus)
        doGet(req, resp);
    }

}
