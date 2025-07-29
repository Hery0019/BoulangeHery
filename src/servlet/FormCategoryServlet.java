package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Category;
import util.Params;
import util.SessionUtils;

public class FormCategoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtils.isUserConnected(req)) {
            resp.sendRedirect("form-login");
            return;
        }

        String action = "update".equals(req.getParameter("action")) ? "update" : "create";
        // Valeurs re-soumises par CategoryServlet après un refus (doublon), le cas échéant
        Category category = (Category) req.getAttribute("submitted");
        if (category == null) {
            category = new Category();
            if ("update".equals(action)) {
                category.setId(Params.requiredInt(req, "id"));
                try {
                    category.find();
                } catch (Exception e) {
                    throw new ServletException(e);
                }
            }
        }

        req.setAttribute("action", action);
        req.setAttribute("category", category);
        req.setAttribute("activeMenuItem", "category");
        req.setAttribute("pageTitle", "Catégorie de recette");

        RequestDispatcher dispatcher = req.getRequestDispatcher("form-category.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Atteint uniquement par forward depuis CategoryServlet.doPost (refus)
        doGet(req, resp);
    }

}
