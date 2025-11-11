package util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

/**
 * Photos de recettes téléversées par les utilisateurs.
 *
 * Les fichiers sont écrits hors du WAR (un redéploiement effacerait le dossier
 * de l'application) dans le répertoire donné par la propriété système
 * {@code boulangerie.picture.dir}, à défaut {@code <catalina.base>/boulangerie-pictures}.
 * Ils sont servis par {@code servlet.PictureServlet} sous {@code /pictures/}.
 *
 * Le nom fourni par le navigateur n'est jamais réutilisé : seul son extension
 * est retenue, et uniquement si elle figure dans {@link #EXTENSIONS}.
 */
public final class Pictures {

    /** Préfixe d'URL des photos téléversées, tel que stocké dans recipe.picture. */
    public static final String URL_PREFIX = "pictures/";

    /** Extensions acceptées, avec le type MIME renvoyé au navigateur. */
    private static final Map<String, String> EXTENSIONS = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "webp", "image/webp",
            "gif", "image/gif");

    private Pictures() {
    }

    /** Répertoire de stockage, créé au besoin. */
    public static Path directory() throws IOException {
        String configured = System.getProperty("boulangerie.picture.dir");
        Path dir;
        if (configured != null && !configured.isBlank()) {
            dir = Paths.get(configured);
        } else {
            String base = System.getProperty("catalina.base", System.getProperty("java.io.tmpdir"));
            dir = Paths.get(base, "boulangerie-pictures");
        }
        Files.createDirectories(dir);
        return dir;
    }

    public static String contentType(String fileName) {
        return EXTENSIONS.get(extensionOf(fileName));
    }

    /**
     * Enregistre la photo soumise et renvoie le chemin à stocker en base
     * ({@code pictures/<nom généré>}), ou {@code null} si aucun fichier n'a été
     * envoyé — cas normal d'une modification qui conserve l'image existante.
     */
    public static String store(HttpServletRequest req, String field) throws IOException, ServletException {
        String contentType = req.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("multipart/")) {
            return null; // formulaire sans photo : rien à enregistrer
        }
        return store(req.getPart(field));
    }

    public static String store(Part part) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }
        String extension = extensionOf(part.getSubmittedFileName());
        if (!EXTENSIONS.containsKey(extension)) {
            throw new BadRequestException(
                    "Format d'image non accepté : utilisez un fichier " + String.join(", ", EXTENSIONS.keySet()) + ".");
        }
        String name = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, directory().resolve(name), StandardCopyOption.REPLACE_EXISTING);
        }
        return URL_PREFIX + name;
    }

    private static String extensionOf(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? "" : fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
