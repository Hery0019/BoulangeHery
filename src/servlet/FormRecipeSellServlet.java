package servlet;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Client;
import dao.Recipe;
import dao.RecipeSell;
import dao.User;
import dao.Vendeur;
import util.Params;
import util.SessionUtils;

public class FormRecipeSellServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtils.isUserConnected(req)) {
            resp.sendRedirect("form-login");
            return;
        }

        String action = "update".equals(req.getParameter("action")) ? "update" : "create";
        // Valeurs re-soumises par RecipeSellServlet après un refus métier, le cas échéant
        RecipeSell recipeSell = (RecipeSell) req.getAttribute("submitted");
        ArrayList<Recipe> recipies;
        ArrayList<Vendeur> vendeurs;
        ArrayList<User> users;
        ArrayList<Client> clients;

        try {
            recipies = Recipe.all();
            vendeurs = Vendeur.all();
            users = User.all();
            clients = Client.all();

            if (recipeSell == null) {
                recipeSell = new RecipeSell();
                if ("update".equals(action)) {
                    recipeSell.setId(Params.requiredInt(req, "id"));
                    recipeSell.find();
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        req.setAttribute("action", action);
        req.setAttribute("recipeSell", recipeSell);
        req.setAttribute("recipies", recipies);
        req.setAttribute("vendeurs", vendeurs);
        req.setAttribute("users", users);
        req.setAttribute("clients", clients);
        req.setAttribute("activeMenuItem", "recipe-sell");
        req.setAttribute("pageTitle", "Vente de Recette");

        RequestDispatcher dispatcher = req.getRequestDispatcher("form-recipe-sell.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Atteint uniquement par forward depuis RecipeSellServlet.doPost (refus métier)
        doGet(req, resp);
    }

}
