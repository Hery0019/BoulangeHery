package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Recipe;
import dao.Step;

public class StepServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");

            if (action != null && action.equals("delete")) {
                int id = Integer.parseInt(req.getParameter("id"));
                Step step = new Step(id);
                step.delete();
            }

            ArrayList<Recipe> recipes = Recipe.all();

            int idRecipe = req.getParameter("searchIdRecipe") == null ? 0
                    : Integer.parseInt(req.getParameter("searchIdRecipe"));
            String minStepNumberStr = req.getParameter("searchMinStepNumber");
            String maxStepNumberStr = req.getParameter("searchMaxStepNumber");
            String instruction = req.getParameter("searchInstruction") == null ? ""
                    : req.getParameter("searchInstruction");
            int minStepNumber = 0;
            int maxStepNumber = 0;

            if (minStepNumberStr != null && !minStepNumberStr.equals("")) {
                minStepNumber = Integer.parseInt(minStepNumberStr);
            }

            if (maxStepNumberStr != null && !maxStepNumberStr.equals("")) {
                maxStepNumber = Integer.parseInt(maxStepNumberStr);
            }

            String minCookTimeStr = req.getParameter("searchMinCookTime");
            String maxCookTimeStr = req.getParameter("searchMaxCookTime");
            LocalTime minCookTime = null;
            LocalTime maxCookTime = null;

            if (minCookTimeStr != null && !minCookTimeStr.equals("")) {
                minCookTime = LocalTime.parse(minCookTimeStr);
            }

            if (maxCookTimeStr != null && !maxCookTimeStr.equals("")) {
                maxCookTime = LocalTime.parse(maxCookTimeStr);
            }

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
        int id = Integer.parseInt(req.getParameter("idStep"));
        int idRecipe = Integer.parseInt(req.getParameter("stepIdRecipe"));
        int number = Integer.parseInt(req.getParameter("stepNumber"));
        String instruction = req.getParameter("stepInstruction");
        LocalTime cookTime = LocalTime.parse(req.getParameter("stepCookTime"));
        Step step = new Step(id, idRecipe, number, instruction, cookTime);

        try {
            if (action != null && action.equals("update")) {
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