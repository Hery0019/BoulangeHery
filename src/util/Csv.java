package util;

import java.util.Locale;

/**
 * Écriture de CSV destinés à un tableur.
 *
 * Séparateur point-virgule et BOM UTF-8 : c'est ce qu'attend Excel en
 * configuration française, sans quoi les accents et les colonnes se perdent.
 */
public final class Csv {

    public static final String SEPARATOR = ";";
    public static final String BOM = "﻿";

    private Csv() {
    }

    /**
     * Échappe une valeur : guillemets doublés, et neutralisation des formules.
     * Une cellule commençant par =, +, - ou @ est interprétée comme une formule
     * par les tableurs ; on la préfixe d'une apostrophe pour qu'elle reste du texte.
     */
    public static String cell(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        if (!text.isEmpty() && "=+-@".indexOf(text.charAt(0)) >= 0) {
            text = "'" + text;
        }
        return '"' + text.replace("\"", "\"\"") + '"';
    }

    /** Ligne complète, valeurs échappées et séparées. */
    public static String line(Object... values) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                line.append(SEPARATOR);
            }
            line.append(cell(values[i]));
        }
        return line.append("\r\n").toString();
    }

    /** Montant avec la virgule décimale attendue par un tableur français. */
    public static String amount(double value) {
        return String.format(Locale.FRENCH, "%.2f", value);
    }
}
