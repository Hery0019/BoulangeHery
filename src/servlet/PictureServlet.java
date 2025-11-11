package servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import util.Pictures;

/**
 * Sert les photos téléversées, stockées hors du WAR (voir {@link Pictures}).
 *
 * Seuls les noms générés à l'enregistrement sont acceptés : tout autre nom est
 * refusé sans toucher au disque, et le chemin résolu doit rester dans le
 * répertoire des photos.
 */
public class PictureServlet extends HttpServlet {

    private static final String NAME_PATTERN = "[a-f0-9]{32}\\.[a-z]{3,4}";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        String name = path == null ? "" : path.substring(1);
        if (!name.matches(NAME_PATTERN)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Path directory = Pictures.directory();
        Path file = directory.resolve(name).normalize();
        if (!file.startsWith(directory) || !Files.isRegularFile(file)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = Pictures.contentType(name);
        if (contentType == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        resp.setContentType(contentType);
        resp.setContentLengthLong(Files.size(file));
        resp.setHeader("Cache-Control", "public, max-age=86400");
        try (OutputStream out = resp.getOutputStream()) {
            Files.copy(file, out);
        }
    }
}
