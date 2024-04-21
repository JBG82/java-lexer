/*
 * Copyright (C) CredaRate Solutions GmbH
 */
package de.geburtig.lexer.tokens;


import de.geburtig.lexer.util.Position;

/**
 * TODO
 *
 * @author jochen.geburtig
 */
public class AnnotationMarker extends Token {
    public AnnotationMarker(final Position position) {
        super("@", position);
    }

    public AnnotationMarker(final Position position, final String spaceAfter) {
        super("@", position, spaceAfter);
    }
}
