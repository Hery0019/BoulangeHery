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

import util.Csrf;

/**
 * Refuse toute requête modifiante (POST, PUT, PATCH, DELETE) qui ne porte pas
 * le jeton anti-CSRF de sa session. Les formulaires l'obtiennent via
 * {@code Csrf.token(request)}.
 */
public class CsrfFilter implements Filter {

    private static final Set<String> MUTATING_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (MUTATING_METHODS.contains(req.getMethod()) && !Csrf.isValid(req)) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Jeton de sécurité manquant ou session expirée : rechargez la page et réessayez.");
            return;
        }
        chain.doFilter(request, response);
    }
}
