package de.geburtig.lexer.tokens;

import de.geburtig.parser.model.Position;

public class SingleLineComment extends Comment {
    public SingleLineComment(final String value, final Position position) {
        super(value, position);
    }

    public SingleLineComment(final String value, final Position position, final String spaceAfter) {
        super(value, position, spaceAfter);
    }
}
