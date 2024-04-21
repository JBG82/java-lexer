/*
 * Copyright (C) CredaRate Solutions GmbH
 */
package de.geburtig.lexer.tokens;

import de.geburtig.parser.model.Position;
import lombok.NonNull;

import java.util.List;

/**
 * Gruppierter Token, der einen Typen repräsentiert (z.B. einen Variablen-, Parameter- oder Methodenrückgabe-Typ)
 *
 * @author jochen.geburtig
 */
public class TypeToken extends GroupToken {

    public static TypeToken of(final @NonNull List<Token> tokens) {
        String value = extractGroupedValueFrom(tokens);
        return new TypeToken(value, tokens.getFirst().getPosition(), tokens.getLast().getSpaceAfter(), tokens);
    }

    private TypeToken(final String value, final Position position, final String spaceAfter, final @NonNull List<Token> tokens) {
        super(value, position, spaceAfter, tokens);
    }

    public String toJavadocOutput() {
        if (getTokens().size() == 1) {
            return getTokens().get(0).getValue();
        } else if (getTokens().size() == 2) {
            if (getTokens().getLast() instanceof GenericToken g) {
                return getTokens().getFirst().getValue() + " " + g.toJavadocPart();
//                if (g.getTokens().size() == 3) {
//                    return getTokens().getFirst().getValue() + " of type " + g.getTokens().get(1).getValue();
//                }
            }
        }
        throw new RuntimeException("Unhandled case: " + getValue());
    }
}
