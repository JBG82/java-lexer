package de.geburtig.lexer.tokens;

import de.geburtig.lexer.util.Position;

public class MultiLineComment extends Comment {
    public MultiLineComment(final String value, final Position position) {
        super(value, position);
    }

    public MultiLineComment(final String value, final Position position, final String spaceAfter) {
        super(value, position, spaceAfter);
    }

    public boolean isJavadocComment() {
        return getValue().startsWith("/**");
    }
}
