package util;

import java.util.List;
import java.util.Locale;

/**
 * Sérialisation JSON minimale pour transmettre des séries de données aux
 * graphiques. Les valeurs sont déposées dans un attribut {@code data-...} de la
 * page, échappé par {@link Html}, puis relues avec {@code JSON.parse} : rien
 * n'est injecté dans un bloc de script.
 */
public final class Json {

    private Json() {
    }

    /** Chaîne JSON échappée, guillemets compris. */
    public static String string(String value) {
        if (value == null) {
            return "null";
        }
        StringBuilder json = new StringBuilder(value.length() + 8).append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"': json.append("\\\""); break;
                case '\\': json.append("\\\\"); break;
                case '\n': json.append("\\n"); break;
                case '\r': json.append("\\r"); break;
                case '\t': json.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        json.append(String.format("\\u%04x", (int) c));
                    } else {
                        json.append(c);
                    }
            }
        }
        return json.append('"').toString();
    }

    public static String number(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    /** Tableau de libellés : {@code ["Pains","Viennoiseries"]}. */
    public static String labels(List<String> values) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            json.append(i == 0 ? "" : ",").append(string(values.get(i)));
        }
        return json.append(']').toString();
    }

    /** Tableau de nombres : {@code [1200.00,850.50]}. */
    public static String numbers(List<Double> values) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            json.append(i == 0 ? "" : ",").append(number(values.get(i)));
        }
        return json.append(']').toString();
    }
}
