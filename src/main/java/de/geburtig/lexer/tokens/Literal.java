package de.geburtig.lexer.tokens;

import de.geburtig.parser.model.Position;

public class Literal extends Token {
    public Literal(final String value, final Position position) {
        super(value, position);
    }

    public Literal(final String value, final Position position, final String spaceAfter) {
        super(value, position, spaceAfter);
    }
}
