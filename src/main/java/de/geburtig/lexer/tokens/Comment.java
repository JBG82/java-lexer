package de.geburtig.lexer.tokens;

import de.geburtig.lexer.util.Position;

public abstract class Comment extends Token {
    public Comment(final String value, final Position position) {
        super(value, position);
    }

    public Comment(final String value, final Position position, final String spaceAfter) {
        super(value, position, spaceAfter);
    }
}
