package de.geburtig.lexer.tokens;

import de.geburtig.lexer.util.Position;

import java.util.Set;

public class Separator extends Token {

    // TODO: Sind "<" und ">" wirklich Separatoren? Könnte in einem anderen Kontext ja auch Operatoren sein (z.B. "x < 5")
    // TODO: Ist "." wirklich ein Separator? Könnte in einem anderen Kontext ja auch Teil eines Literals sein (z.B. "x = 5.8")
    private static final Set<Character> SEPARATORS = Set.of(';', ',', '(', ')', '{', '}', '[', ']', '.');
//    private static final Set<Character> SEPARATORS = Set.of(';', ',', '(', ')', '{', '}', '[', ']', '<', '>');

    /**
     * Liefert true, wenn der übergebene char-Wert einem definierten Separator entspricht.
     * @param c char
     * @return boolean
     */
    public static boolean isSeparator(final char c) {
        return SEPARATORS.contains(c);
    }

    /**
     * Liefert true, wenn der übergebene String-Wert einem definierten Separator entspricht.
     * @param value String
     * @return boolean
     */
    public static boolean isSeparator(final String value) {
        if (value.length() == 1) {
            return isSeparator(value.charAt(0));
        }
        return false;
    }

    public Separator(final String value, final Position position) {
        super(value, position);
    }

    public Separator(final String value, final Position position, final String spaceAfter) {
        super(value, position, spaceAfter);
    }
}
