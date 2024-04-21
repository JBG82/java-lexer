package de.geburtig.lexer.tokens;

import de.geburtig.parser.model.Position;
import lombok.NonNull;

import java.util.List;

public class ArrayToken extends GroupToken {

    public static ArrayToken of(final @NonNull List<Token> tokens) {
        String value = extractGroupedValueFrom(tokens);
//        if (!"[".equals(tokens.getFirst().getValue())) {
//            throw new IllegalArgumentException("\"[\" needs to be the first token in an ArrayToken");
//        }
        if (!"]".equals(tokens.getLast().getValue())) {
            throw new IllegalArgumentException("\"]\" needs to be the last token in an ArrayToken");
        }
        return new ArrayToken(value, tokens.getFirst().getPosition(), tokens.getLast().getSpaceAfter(), tokens);
    }

    private ArrayToken(final String value, final Position position, final String spaceAfter, final @NonNull List<Token> tokens) {
        super(value, position, spaceAfter, tokens);
    }

    public String toJavadocOutput() {
        if (getTokens().size() == 3 && getTokens().getLast().getValue().equals("]") && getTokens().get(getTokens().size() - 2).getValue().equals("[")) {
            return "Array of type " + getTokens().get(0).getValue();
        }
        throw new RuntimeException("Unhandled case: " + getValue());
    }

}
