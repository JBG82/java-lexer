/*
 * Copyright (C) CredaRate Solutions GmbH
 */
package de.geburtig.lexer.tokens;

import de.geburtig.parser.model.Position;

import java.util.List;

/**
 * Gruppe von Tokens, die eine Klassen-, Methoden- oder Attribut-Annotation repräsentiert
 *
 * @author jochen.geburtig
 */
public class AnnotationToken extends GroupToken {

    public static AnnotationToken of(final List<Token> tokens) {
        if (!"@".equals(tokens.getFirst().getValue())) {
            throw new IllegalArgumentException("First token in AnnotationToken needs to be \"@\"");
        }
        String value = extractGroupedValueFrom(tokens);
        return new AnnotationToken(value, tokens.getFirst().getPosition(), tokens.getLast().getSpaceAfter(), tokens);
    }

    private AnnotationToken(final String value, final Position position, final String spaceAfter, final List<Token> tokens) {
        super(value, position, spaceAfter, tokens);
    }
}
