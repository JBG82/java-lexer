package de.geburtig.lexer.tokens;

import de.geburtig.parser.model.Position;
import lombok.NonNull;

import java.util.List;

public class LiteralToken extends GroupToken {

    public static LiteralToken of(final @NonNull List<Token> tokens) {
        String value = extractGroupedValueFrom(tokens);
        return new LiteralToken(value, tokens.getFirst().getPosition(), tokens.getLast().getSpaceAfter(), tokens);
    }

    private LiteralToken(final String value, final Position position, final String spaceAfter, final @NonNull List<Token> tokens) {
        super(value, position, spaceAfter, tokens);
    }
}
