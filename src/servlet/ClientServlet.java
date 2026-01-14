package servlet;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.Client;
import dao.DuplicateEntityException;
import dao.EntityInUseException;
import util.Params;

/** Clients : liste, recherche, création, modification et suppression. */
public class ClientServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String term = Params.string(req, "searchTerm", "");
            ArrayList<Client> clients = Client.search(term);
            req.setAttribute("clients", clients);
            req.setAttribute("activeMenuItem", "client");
            req.setAttribute("pageTitle", "Clients");
            RequestDispatcher dispatcher = req.getRequestDispatcher("client.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            Client client = new Client(Params.requiredInt(req, "id"));
            try {
                client.delete();
            } catch (EntityInUseException e) {
                req.setAttribute("errorMessage", e.getMessage());
                doGet(req, resp);
                return;
            } catch (Exception e) {
                throw new ServletException(e);
            }
            resp.sendRedirect("client");
            return;
        }

        boolean update = "update".equals(action);
        Client client = new Client();
        client.setId(Params.intValue(req, "idClient", 0));
        client.setFirstname(Params.requiredString(req, "clientFirstname"));
        client.setLastname(Params.requiredString(req, "clientLastname"));
        client.setPhone(Params.string(req, "clientPhone", ""));
        client.setEmail(Params.string(req, "clientEmail", ""));

        try {
            if (update) {
                client.update();
            } else {
                client.create();
            }
        } catch (DuplicateEntityException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("submitted", client);
            req.getRequestDispatcher(update ? "form-client?action=update&id=" + client.getId() : "form-client")
                    .forward(req, resp);
            return;
        } catch (Exception e) {
            throw new ServletException(e);
        }
        resp.sendRedirect("client");
    }
}
