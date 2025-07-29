package servlet;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Category;
import dao.EntityInUseException;
import util.Params;

public class CategoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ArrayList<Category> categories = Category.all();
            req.setAttribute("categories", categories);
            req.setAttribute("activeMenuItem", "category");
            req.setAttribute("pageTitle", "Catégorie de recette");
            RequestDispatcher dispatcher = req.getRequestDispatcher("category.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            Category category = new Category(Params.requiredInt(req, "id"));
            try {
                category.delete();
            } catch (EntityInUseException e) {
                req.setAttribute("errorMessage", e.getMessage());
                doGet(req, resp);
                return;
            } catch (Exception e) {
                throw new ServletException(e);
            }
            resp.sendRedirect("category");
            return;
        }

        int id = Params.intValue(req, "idCategory", 0);
        String name = Params.requiredString(req, "categoryName");
        Category category = new Category(id, name);

        try {
            if ("update".equals(action)) {
                category.update();
            } else {
                category.create();
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        resp.sendRedirect("category");
    }

}
