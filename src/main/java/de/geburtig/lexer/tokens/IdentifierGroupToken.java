package de.geburtig.lexer.tokens;

import de.geburtig.lexer.util.Position;
import lombok.NonNull;

import java.util.List;

/**
 * Ein gruppiertes Token aus einzelnen Elementen, die mit einem "." aneinander gereiht wurden.
 * Beispiele:
 *   "this.test = 5;"
 *   "package de.geburtig.parser.model.tokens;"
 *   "java.lang.String test = \"blah\";"
 */
public class IdentifierGroupToken extends GroupToken {

    public static IdentifierGroupToken of(final @NonNull List<Token> tokens) {
        String value = extractGroupedValueFrom(tokens);
        return new IdentifierGroupToken(value, tokens.getFirst().getPosition(), tokens.getLast().getSpaceAfter(), tokens);
    }

    private IdentifierGroupToken(final String value, final Position position, final String spaceAfter, final @NonNull List<Token> tokens) {
        super(value, position, spaceAfter, tokens);
    }

}
