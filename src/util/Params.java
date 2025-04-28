package util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Lecture typée des paramètres de requête.
 *
 * Convention : un paramètre absent ou vide vaut la valeur par défaut fournie
 * (ou {@code null} pour les dates/heures) ; un paramètre présent mais mal
 * formé lève une {@link BadRequestException} (réponse 400), jamais une
 * NumberFormatException anonyme (page 500).
 */
public final class Params {

    private Params() {
    }

    /** Valeur brute, ou {@code null} si absente ou vide (après trim). */
    public static String raw(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        }
        value = value.trim();
        return value.isEmpty() ? null : value;
    }

    public static String string(HttpServletRequest req, String name, String defaultValue) {
        String value = raw(req, name);
        return value == null ? defaultValue : value;
    }

    public static String requiredString(HttpServletRequest req, String name) {
        String value = raw(req, name);
        if (value == null) {
            throw missing(name);
        }
        return value;
    }

    public static int intValue(HttpServletRequest req, String name, int defaultValue) {
        String value = raw(req, name);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw invalid(name, "entier attendu");
        }
    }

    public static int requiredInt(HttpServletRequest req, String name) {
        if (raw(req, name) == null) {
            throw missing(name);
        }
        return intValue(req, name, 0);
    }

    public static double doubleValue(HttpServletRequest req, String name, double defaultValue) {
        String value = raw(req, name);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw invalid(name, "nombre attendu");
        }
    }

    public static double requiredDouble(HttpServletRequest req, String name) {
        if (raw(req, name) == null) {
            throw missing(name);
        }
        return doubleValue(req, name, 0.0);
    }

    /** Date ISO (yyyy-MM-dd) ou {@code null} si absente. */
    public static LocalDate date(HttpServletRequest req, String name) {
        String value = raw(req, name);
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw invalid(name, "date attendue (aaaa-mm-jj)");
        }
    }

    public static LocalDate requiredDate(HttpServletRequest req, String name) {
        LocalDate value = date(req, name);
        if (value == null) {
            throw missing(name);
        }
        return value;
    }

    /** Heure ISO (HH:mm ou HH:mm:ss) ou {@code null} si absente. */
    public static LocalTime time(HttpServletRequest req, String name) {
        String value = raw(req, name);
        if (value == null) {
            return null;
        }
        try {
            return LocalTime.parse(value);
        } catch (DateTimeParseException e) {
            throw invalid(name, "heure attendue (hh:mm)");
        }
    }

    public static LocalTime requiredTime(HttpServletRequest req, String name) {
        LocalTime value = time(req, name);
        if (value == null) {
            throw missing(name);
        }
        return value;
    }

    private static BadRequestException missing(String name) {
        return new BadRequestException("Paramètre obligatoire manquant : " + name);
    }

    private static BadRequestException invalid(String name, String expected) {
        return new BadRequestException("Paramètre invalide : " + name + " (" + expected + ")");
    }
}
