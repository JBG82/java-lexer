package de.geburtig.lexer.tokens;

import de.geburtig.lexer.util.Position;

/**
 * Repräsentiert einen Bezeichner auf unterster Code-Ebene (z.B. einen Variablen- oder Klassennamen).
 */
public class Identifier extends Token {
    public Identifier(final String value, final Position position) {
        super(value, position);
    }

    public Identifier(final String value, final Position position, final String spaceAfter) {
        super(value, position, spaceAfter);
    }
}
