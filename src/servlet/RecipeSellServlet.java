package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Category;
import dao.Recipe;
import dao.RecipeSell;
import  dao.Vendeur;
import dao.User;

public class RecipeSellServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");

            if (action != null && action.equals("delete")) {
                int id = Integer.parseInt(req.getParameter("id"));
                RecipeSell recipeSell = new RecipeSell(id);
                recipeSell.delete();
            }

            ArrayList<Recipe> recipies = Recipe.all();
            ArrayList<Category> categories = Category.all();
            ArrayList<User> users = User.all();
            ArrayList<Vendeur> vendeurs = Vendeur.all();

            int idRecipe = req.getParameter("searchIdRecipe") == null ? 0
                    : Integer.parseInt(req.getParameter("searchIdRecipe"));

            int idCategory = req.getParameter("searchIdCategory") == null ? 0
                    : Integer.parseInt(req.getParameter("searchIdCategory"));

            int idUser = req.getParameter("searchIdUser") == null ? 0
                    : Integer.parseInt(req.getParameter("searchIdUser"));

            String minSellDateStr = req.getParameter("searchMinSellDate");
            String maxSellDateStr = req.getParameter("searchMaxSellDate");
            LocalDate minSellDate = null;
            LocalDate maxSellDate = null;

            String minCombienStr = req.getParameter("searchMinCombien") == null ? "0"
                    : req.getParameter("searchMinCombien");

            String maxCombienStr = req.getParameter("searchMaxCombien") == null ? "0"
                    : req.getParameter("searchMaxCombien");

            int minCombien = Integer.valueOf(minCombienStr);
            int maxCombien = Integer.valueOf(maxCombienStr);

            String minArgentStr = req.getParameter("searchMinArgent") == null ? "0"
                    : req.getParameter("searchMinArgent");

            String maxArgentStr = req.getParameter("searchMaxArgent") == null ? "0"
                    : req.getParameter("searchMaxArgent");

            double minArgent = Double.parseDouble(minArgentStr);
            double maxArgent = Double.parseDouble(maxArgentStr);

            String minResteStr = req.getParameter("searchMinReste") == null ? "0"
                    : req.getParameter("searchMinReste");

            String maxResteStr = req.getParameter("searchMaxReste") == null ? "0"
                    : req.getParameter("searchMaxReste");

            double minReste = Double.parseDouble(minResteStr);
            double maxReste = Double.parseDouble(maxResteStr);

            if (minSellDateStr != null && !minSellDateStr.equals("")) {
                minSellDate = LocalDate.parse(minSellDateStr);
            }

            if (maxSellDateStr != null && !maxSellDateStr.equals("")) {
                maxSellDate = LocalDate.parse(maxSellDateStr);
            }

            System.out.println("searchIdRecipe: " + idRecipe);
            System.out.println("searchIdCategory: " + idCategory);
            System.out.println("searchMinSellDate: " + minSellDateStr);


            ArrayList<RecipeSell> recipeSells = RecipeSell.search(idRecipe, idCategory, idUser, minCombien, maxCombien, minArgent, maxArgent,
                    minReste, maxReste, minSellDate, maxSellDate);

            for (RecipeSell recipeSell : recipeSells) {
                System.out.println(recipeSell);
            }

            req.setAttribute("recipeSells", recipeSells);
            req.setAttribute("categories", categories);
            req.setAttribute("recipies", recipies);
            req.setAttribute("vendeurs", vendeurs);
            req.setAttribute("users", users);
            req.setAttribute("activeMenuItem", "recipe-sell");
            req.setAttribute("pageTitle", "Vente de recette");

            RequestDispatcher dispatcher = req.getRequestDispatcher("recipe-sell.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String action = req.getParameter("action");
        int id = Integer.parseInt(req.getParameter("idRecipeSell"));
        int idRecipe = Integer.parseInt(req.getParameter("idRecipe"));
        int idVendeur = Integer.parseInt(req.getParameter("recipeSellerId"));
    
        Recipe recipe = null;
        int idCategory = 1;
        double resteCalculer = 0.0;
    
        try {
            recipe = Recipe.findById(idRecipe);
            idCategory = Category.findById(recipe.getIdCategory()).getId();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    
        int idUser = Integer.parseInt(req.getParameter("recipeSellIdUser"));
        int combien = Integer.parseInt(req.getParameter("recipeSellCombien"));
        double argent = Double.parseDouble(req.getParameter("recipeSellArgent"));
        resteCalculer = argent - combien * recipe.getPrice();
        System.out.println("Reste calculé : " + resteCalculer);
    
        LocalDate sellDate = LocalDate.parse(req.getParameter("recipeSellDate"));
        RecipeSell recipeSell = new RecipeSell(id, idVendeur, idRecipe, idCategory, idUser, combien, argent, 0.0, sellDate);
    
        try {
            if (action != null && action.equals("update")) {
                recipeSell.update();
            } else if (action != null && action.equals("create")) {
                recipeSell.create();
            }
        } catch (Exception e) {
            resp.sendRedirect("form-recipe-sell.jsp?error=true");
            return;
        }
    
        resp.sendRedirect("recipe-sell");
    }
    

}
