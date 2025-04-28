package servlet;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Recipe;
import dao.Step;
import util.Params;

public class StepServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");

            if ("delete".equals(action)) {
                Step step = new Step(Params.requiredInt(req, "id"));
                step.delete();
            }

            ArrayList<Recipe> recipes = Recipe.all();

            int idRecipe = Params.intValue(req, "searchIdRecipe", 0);
            int minStepNumber = Params.intValue(req, "searchMinStepNumber", 0);
            int maxStepNumber = Params.intValue(req, "searchMaxStepNumber", 0);
            String instruction = Params.string(req, "searchInstruction", "");
            LocalTime minCookTime = Params.time(req, "searchMinCookTime");
            LocalTime maxCookTime = Params.time(req, "searchMaxCookTime");

            ArrayList<Step> steps = Step.search(idRecipe, minStepNumber, maxStepNumber, minCookTime, maxCookTime,
                    instruction);

            req.setAttribute("steps", steps);
            req.setAttribute("recipes", recipes);
            req.setAttribute("activeMenuItem", "step");
            req.setAttribute("pageTitle", "Etape");

            RequestDispatcher dispatcher = req.getRequestDispatcher("step.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String action = req.getParameter("action");
        int id = Params.intValue(req, "idStep", 0);
        int idRecipe = Params.requiredInt(req, "stepIdRecipe");
        int number = Params.requiredInt(req, "stepNumber");
        String instruction = Params.requiredString(req, "stepInstruction");
        LocalTime cookTime = Params.requiredTime(req, "stepCookTime");
        Step step = new Step(id, idRecipe, number, instruction, cookTime);

        try {
            if ("update".equals(action)) {
                step.update();
            } else {
                step.create();
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        resp.sendRedirect("step");
    }

}
