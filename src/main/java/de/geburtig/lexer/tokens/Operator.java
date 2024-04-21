package de.geburtig.lexer.tokens;

import de.geburtig.lexer.util.Position;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class Operator extends Token {

    // TODO: Hier fehlen noch einige (z.B. "?" und ":" bei unären Operatoren, In-/Dekrementoperatoren wie "++" und "--")
    private static final Set<String> OPERATORS = Set.of(
            "+", "-", "*", "/",                 // Berechnungsoperatoren
            "==", "!=", "<", ">", "<=", ">=",   // Vergleichsoperatoren
            "=", "+=", "-=", "*=", "/=",        // Zuweisungsoperatoren
            "&", "&&", "|", "||", "!",          // Logische Operatoren
            "..."                               // Varargs-Operator
    );

    /** Set an chars, mit denen mindestens ein Operator beginnt */
    private static final Set<Character> OPERATOR_CHARS = OPERATORS.stream().map(v -> v.charAt(0)).collect(toSet());

    /** Set mit den ersten zwei Zeichen aller Operatoren der Länge 3 */
    private static final Set<String> UNFINISHED_OPERATORS = OPERATORS.stream().filter(o -> o.length() == 3).map(o -> o.substring(0, 2)).collect(toSet());

    /**
     * Prüft, ob es Operatoren mit mehr als einem Zeichen gibt, die mit dem übergebenen char-Wert beginnen.
     * @param c char
     * @return boolean
     */
    public static boolean isOperatorOrStarting(final char c) {
        return OPERATOR_CHARS.contains(c);
    }

    /**
     * Prüft, ob der übergebene Wert der beginnende Bestandteil eines existierenden Operators ist.
     * Wird benötigt bei Operatoren mit mehr als zwei Zeichen länge (z.B. "...").
     * Wirft eine RuntimeException, wenn der übergebene Wert mehr als zwei Zeichen besitzt.
     * @param value Zu prüfender String
     * @return boolean
     */
    public static boolean isOnTheWay(final String value) {
        if (value.length() != 2) {
            throw new RuntimeException("Something's wrong here, should not occur!");
        }
        return UNFINISHED_OPERATORS.contains(value);
    }

    public static boolean isOperator(final String value) {
        return OPERATORS.contains(value);
    }

    public Operator(final String value, final Position position) {
        super(value, position);
    }

    public Operator(final String value, final Position position, final String spaceAfter) {
        super(value, position, spaceAfter);
    }

    /**
     * True, wenn es sich um einen Operator handelt, der im Zusammenhang mit der Deklaration von Generics verwendet
     * werden, also "<" oder ">".
     * @return boolean
     */
    public boolean isGenericOperator() {
        return "<".equals(getValue()) || ">".equals(getValue());
    }
}
