package util;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Affichage des montants.
 *
 * L'application manipule une seule devise, l'ariary : les prix saisis dans les
 * données (1500 pour une baguette) sont des ariary, et les pages affichaient
 * indifféremment « € » ou « Ar » pour les mêmes montants. Tout passe désormais
 * par {@link #format(double)}.
 */
public final class Money {

    /** Symbole affiché après le montant. */
    public static final String CURRENCY = "Ar";

    private Money() {
    }

    /** Montant lisible, séparateur de milliers et deux décimales : « 1 600,00 Ar ». */
    public static String format(double amount) {
        return String.format(Locale.FRENCH, "%,.2f %s", amount, CURRENCY);
    }

    public static String format(BigDecimal amount) {
        return format(amount == null ? 0.0 : amount.doubleValue());
    }

    /**
     * Montant destiné à la valeur d'un {@code <input type="number">} : deux
     * décimales et un point, seul format accepté par le navigateur.
     */
    public static String plain(double amount) {
        return String.format(Locale.ROOT, "%.2f", amount);
    }

    public static String plain(BigDecimal amount) {
        return plain(amount == null ? 0.0 : amount.doubleValue());
    }
}
