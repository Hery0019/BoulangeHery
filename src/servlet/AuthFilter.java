package servlet;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.User;
import util.SessionUtils;

/**
 * Contrôle d'accès central.
 *
 * Deux règles se superposent :
 *   1. la consultation (GET sans action de mutation) des pages de
 *      {@link #PUBLIC_PATHS} est ouverte à tous, comme les ressources
 *      statiques et les photos ;
 *   2. tout le reste exige une session, et le rôle du compte doit figurer
 *      parmi ceux autorisés pour le chemin demandé ({@link #ROLES}).
 *
 * Un chemin absent de {@link #ROLES} n'est ouvert qu'aux administrateurs :
 * une nouvelle page est fermée par défaut tant qu'elle n'y est pas déclarée.
 */
public class AuthFilter implements Filter {

    /** Pages consultables sans connexion, en GET uniquement. */
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/", "/index.jsp",
            "/recipe", "/recipe-details",
            "/review",
            "/category", "/ingredient", "/step",
            "/form-login", "/form-login.jsp");

    private static final Set<String> CATALOG = Set.of(User.ADMIN, User.BOULANGER);
    private static final Set<String> SALES = Set.of(User.ADMIN, User.VENDEUR);
    private static final Set<String> ANY = Set.of(User.ADMIN, User.BOULANGER, User.VENDEUR);

    /** Rôles admis par chemin, au-delà de la consultation publique. */
    private static final Map<String, Set<String>> ROLES = Map.ofEntries(
            Map.entry("/recipe", CATALOG),
            Map.entry("/form-recipe", CATALOG),
            Map.entry("/recipe-details", CATALOG),
            Map.entry("/recipe-ingredient", CATALOG),
            Map.entry("/form-recipe-ingredient", CATALOG),
            Map.entry("/step", CATALOG),
            Map.entry("/form-step", CATALOG),
            Map.entry("/category", CATALOG),
            Map.entry("/form-category", CATALOG),
            Map.entry("/ingredient", CATALOG),
            Map.entry("/form-ingredient", CATALOG),
            Map.entry("/recipe-stock", CATALOG),
            Map.entry("/ingredient-stock", CATALOG),
            Map.entry("/production", CATALOG),
            Map.entry("/form-production", CATALOG),
            Map.entry("/recipe-loss", CATALOG),
            Map.entry("/form-recipe-loss", CATALOG),
            Map.entry("/recipe-price-history", CATALOG),
            Map.entry("/recipe-sell", SALES),
            Map.entry("/form-recipe-sell", SALES),
            Map.entry("/commission", SALES),
            Map.entry("/client", SALES),
            Map.entry("/export", SALES),
            Map.entry("/receipt", SALES),
            Map.entry("/form-client", SALES),
            // Le tableau de bord n'affiche que ce que le rôle autorise (voir DashboardServlet).
            Map.entry("/dashboard", ANY),
            // Un avis peut être rédigé par n'importe quel employé connecté.
            Map.entry("/review", ANY),
            Map.entry("/form-review", ANY));

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
        if ("/login".equals(path)) { // connexion (POST) et déconnexion (POST)
            chain.doFilter(request, response);
            return;
        }
        if (!mutation && PUBLIC_PATHS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }
        if (!SessionUtils.isUserConnected(req)) {
            if (readOnly) {
                resp.sendRedirect(req.getContextPath() + "/form-login");
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Connexion requise");
            }
            return;
        }
        if (!SessionUtils.hasAnyRole(req, ROLES.getOrDefault(path, Set.of(User.ADMIN)).toArray(new String[0]))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Votre rôle ne donne pas accès à cette page.");
            return;
        }
        chain.doFilter(request, response);
    }
}
