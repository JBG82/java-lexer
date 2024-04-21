/*
 * Copyright (C) CredaRate Solutions GmbH
 */
package de.geburtig.lexer.tokens;

import de.geburtig.lexer.util.Position;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Gruppierung von einzelnen Tokens, die offensichtlich zueinander gehören.
 *
 * @author jochen.geburtig
 */
public abstract class GroupToken extends Token {

    @Getter
    private final List<Token> tokens = new ArrayList<>();

    protected static String extractGroupedValueFrom(final @NonNull List<Token> tokens) {
        if (tokens.isEmpty()) throw new IllegalArgumentException("GroupToken with empty token list is bullshit");
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < tokens.size() - 1; ++i) {
            b.append(tokens.get(i).getValue()).append(tokens.get(i).getSpaceAfter());
        }
        b.append(tokens.getLast().getValue());
        return b.toString();
    }

    protected GroupToken(final String value, final Position position, final String spaceAfter, final @NonNull List<Token> tokens) {
        super(value, position, spaceAfter);
        this.tokens.addAll(tokens);
    }
}
