package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.RecipeSell;
import util.BadRequestException;
import util.Params;

/** Ticket d'une vente, mis en page pour l'impression. */
public class ReceiptServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Params.requiredInt(req, "id");
        RecipeSell sell;
        try {
            sell = RecipeSell.findById(id);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        if (sell == null) {
            throw new BadRequestException("Vente inconnue : " + id);
        }
        req.setAttribute("sell", sell);
        req.setAttribute("pageTitle", "Ticket de vente");

        RequestDispatcher dispatcher = req.getRequestDispatcher("receipt.jsp");
        dispatcher.forward(req, resp);
    }
}
