package servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import util.BadRequestException;

/**
 * Dernier rempart : journalise toute exception non gérée et renvoie une
 * réponse HTTP propre (400 pour une requête invalide, 500 sinon) au lieu de
 * laisser Tomcat afficher la pile d'appels au visiteur.
 */
public class ErrorFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(ErrorFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException e) {
            Throwable root = rootCause(e);
            if (resp.isCommitted()) {
                // Trop tard pour changer la réponse : on journalise et on laisse remonter.
                LOG.log(Level.SEVERE, "Erreur après envoi de la réponse sur " + describe(req), e);
                throw e;
            }
            if (root instanceof BadRequestException) {
                LOG.log(Level.INFO, "Requête invalide sur " + describe(req) + " : " + root.getMessage());
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, root.getMessage());
                return;
            }
            LOG.log(Level.SEVERE, "Erreur non gérée sur " + describe(req), e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static Throwable rootCause(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }

    private static String describe(HttpServletRequest req) {
        String query = req.getQueryString();
        return req.getMethod() + " " + req.getRequestURI() + (query == null ? "" : "?" + query);
    }
}
