package servlet;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import util.SessionUtils;

/**
 * Contrôle d'accès central.
 *
 * Règle : tout est réservé aux utilisateurs connectés, sauf la consultation
 * (GET sans action de mutation) des pages listées dans {@link #PUBLIC_PATHS},
 * la page de connexion et les ressources statiques.
 *
 * Les pages financières (ventes, commissions, historique des prix) ne sont
 * volontairement pas publiques.
 */
public class AuthFilter implements Filter {

    /** Pages consultables sans connexion, en GET uniquement. */
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/", "/index.jsp",
            "/recipe", "/recipe-details",
            "/review",
            "/category", "/ingredient", "/step",
            "/form-login", "/form-login.jsp");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        String method = req.getMethod();
        boolean readOnly = "GET".equals(method) || "HEAD".equals(method);
        boolean mutation = !readOnly || "delete".equals(req.getParameter("action"));

        if (path.startsWith("/assets/") || path.startsWith("/pictures/")) {
            chain.doFilter(request, response);
            return;
        }
        if ("/login".equals(path)) { // connexion (POST) et déconnexion (GET)
            chain.doFilter(request, response);
            return;
        }
        if (!mutation && PUBLIC_PATHS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }
        if (SessionUtils.isUserConnected(req)) {
            chain.doFilter(request, response);
            return;
        }

        if (readOnly) {
            resp.sendRedirect(req.getContextPath() + "/form-login");
        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Connexion requise");
        }
    }
}
