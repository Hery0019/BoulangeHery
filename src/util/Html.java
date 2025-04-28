package util;

/**
 * Échappement HTML pour les JSP : toute valeur dynamique affichée passe par
 * {@link #esc(Object)}. Neutralise &lt; &gt; &amp; " ' pour un contenu texte
 * ou une valeur d'attribut entre guillemets.
 */
public final class Html {

    private Html() {
    }

    public static String esc(Object value) {
        if (value == null) {
            return "";
        }
        String s = String.valueOf(value);
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}
