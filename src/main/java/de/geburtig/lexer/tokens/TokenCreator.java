package de.geburtig.lexer.tokens;

import de.geburtig.lexer.util.Position;
import lombok.NonNull;

import static de.geburtig.lexer.tokens.Keyword.KEYWORDS;

public class TokenCreator {

    public static Token from(final @NonNull String value, final @NonNull Position position) {
        if (KEYWORDS.contains(value)) {
            return new Keyword(value, position);
        } else if (isLiteral(value)) {
            return new Literal(value, position);
        } else if (value.length() == 1 && Separator.isSeparator(value.charAt(0))) {
            return new Separator(value, position);
        }
        return new UnspecifiedToken(value, position);
    }

    private static boolean isLiteral(final String value) {
        if (value == null || value.isEmpty()) return false;
        if ("null".equals(value)) return true;
        if (value.charAt(0) >= '0' && value.charAt(0) <= '9') return true;
        return false;
    }
}
