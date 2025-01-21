package servlet;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;

import dao.Category;
import dao.Perfume;
import dao.Recipe;
import dao.Vendeur;
import dao.User;
import dao.RecipeSell;
import util.SessionUtils;

public class FormRecipeSellServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtils.isUserConnected(req)) {
            resp.sendRedirect("form-login");
            return;
        }

        String action = req.getParameter("action");
        RecipeSell recipeSell = new RecipeSell();
        ArrayList<Category> categories;
        ArrayList<Perfume> perfumes;
        ArrayList<Recipe> recipies;
        ArrayList<Vendeur> vendeurs;


        ArrayList<User> users;


        try {
            categories = Category.all();
            perfumes = Perfume.all();
            recipies = Recipe.all();
            vendeurs = Vendeur.all();
            users = User.all();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        if (action != null && action.equals("update")) {
            int id = Integer.parseInt(req.getParameter("id"));
            recipeSell.setId(id);
            try {
                recipeSell.find();
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else {
            action = "create";
        }

        req.setAttribute("action", action);
        req.setAttribute("recipeSell", recipeSell);
        req.setAttribute("recipies", recipies);
        req.setAttribute("vendeurs", vendeurs);
        req.setAttribute("users", users);
        req.setAttribute("perfumes", perfumes);
        req.setAttribute("activeMenuItem", "recipe-sell");
        req.setAttribute("pageTitle", "Vente de Recette");

        RequestDispatcher dispatcher = req.getRequestDispatcher("form-recipe-sell.jsp");
        dispatcher.forward(req, resp);
    }

}