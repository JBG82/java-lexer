package de.geburtig.lexer.tokens;

import de.geburtig.lexer.util.Position;

/**
 * Repräsentiert einen Token, der zum Zeitpunkt seiner Erstellung noch nicht näher spezifiziert werden konnte.
 */
public class UnspecifiedToken extends Token {
    public UnspecifiedToken(final String value, final Position position) {
        super(value, position);
    }

    public UnspecifiedToken(final String value, final Position position, final String spaceAfter) {
        super(value, position, spaceAfter);
    }
}
