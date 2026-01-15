package util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/** Construction de liens qui conservent les critères de la page courante. */
public final class Links {

    private Links() {
    }

    /**
     * Chaîne de requête reprenant les paramètres actuels, sauf ceux nommés :
     * permet à un lien de pagination ou de tri de garder la recherche en cours.
     * Renvoie une chaîne vide ou terminée par {@code &}, prête à être suivie du
     * paramètre à ajouter.
     */
    public static String queryWithout(HttpServletRequest req, String... excluded) {
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
            if (Arrays.asList(excluded).contains(entry.getKey())) {
                continue;
            }
            for (String value : entry.getValue()) {
                if (value == null || value.isEmpty()) {
                    continue;
                }
                query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8))
                        .append('&');
            }
        }
        return query.toString();
    }
}
