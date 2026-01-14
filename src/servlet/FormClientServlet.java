package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Client;
import util.Params;

/** Formulaire client, vide ou pré-rempli selon l'action. */
public class FormClientServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = "update".equals(req.getParameter("action")) ? "update" : "create";
        // Valeurs re-soumises par ClientServlet après un refus (email en double)
        Client client = (Client) req.getAttribute("submitted");
        if (client == null) {
            client = new Client();
            if ("update".equals(action)) {
                client.setId(Params.requiredInt(req, "id"));
                try {
                    client.find();
                } catch (Exception e) {
                    throw new ServletException(e);
                }
            }
        }
        req.setAttribute("action", action);
        req.setAttribute("client", client);
        req.setAttribute("activeMenuItem", "client");
        req.setAttribute("pageTitle", "Clients");

        RequestDispatcher dispatcher = req.getRequestDispatcher("form-client.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Atteint uniquement par forward depuis ClientServlet.doPost (refus)
        doGet(req, resp);
    }
}
