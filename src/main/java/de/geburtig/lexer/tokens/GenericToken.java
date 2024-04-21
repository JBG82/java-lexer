/*
 * Copyright (C) CredaRate Solutions GmbH
 */
package de.geburtig.lexer.tokens;

import de.geburtig.parser.model.Position;
import de.geburtig.parser.util.PeekableIterator;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gruppiertes Token mit allen Bestandteilen, die sich innerhalb der spitzen Klammern einer Generic-Deklaration befinden
 *
 * @author jochen.geburtig
 */
public class GenericToken extends GroupToken {

    /** Tokens als Container der einzelnen Listenelemente des generischen Ausdrucks */
    @Getter
    private final List<Token> contentTokens;

    public static GenericToken of(final @NonNull List<Token> tokens) {
        if (!"<".equals(tokens.getFirst().getValue())) {
            throw new IllegalArgumentException("First token in GenericToken needs to be \"<\"");
        }
        if (!">".equals(tokens.getLast().getValue())) {
            throw new IllegalArgumentException("Last token in GenericToken needs to be \">\"");
        }
        String value = extractGroupedValueFrom(tokens);
        return new GenericToken(value, tokens.getFirst().getPosition(), tokens.getLast().getSpaceAfter(), tokens);
    }

    private GenericToken(final String value, final Position position, final String spaceAfter, final @NonNull List<Token> tokens) {
        super(value, position, spaceAfter, tokens);
        this.contentTokens = initContentTokensFrom(tokens);
    }

    private static List<Token> initContentTokensFrom(final List<Token> tokens) {
        List<Token> contentTokens = new ArrayList<>();
        int braceDepth = 0;
        String caught = "";
        Position pos = null;
        for (Token token : tokens) {
            String tokenValue = token.getValue();
            if ("<".equals(tokenValue)) {
                if (braceDepth > 0) {
                    caught += tokenValue;
                }
                ++braceDepth;
            } else if (">".equals(tokenValue)) {
                if (braceDepth == 1) {
                    contentTokens.add(new UnspecifiedToken(caught,pos));
                } else if (braceDepth > 1) {
                    caught += tokenValue;
                }
                --braceDepth;
            } else if (",".equals(tokenValue) && braceDepth == 1) {
                contentTokens.add(new UnspecifiedToken(caught, pos));
                caught = "";
                pos = null;
            } else {
                if (caught.isEmpty()) {
                    pos = token.getPosition();
                }
                caught += tokenValue;
            }
        }
        return contentTokens;
    }

    public String toJavadocPart() {
        if (contentTokens.size() == 1) {
            return "of type " + contentTokens.getFirst().getValue();
        } else if (contentTokens.size() == 2) {
            return "with key type " + contentTokens.getFirst().getValue() + " and value type " + contentTokens.getLast().getValue();
        } else if (contentTokens.size() > 2) {
            return "of types " + contentTokens.stream().map(Token::getValue).collect(Collectors.joining(", "));
        }
        return null;
    }

    public List<String> extractTypeNames() {
        ArrayList<String> result = new ArrayList<>();

        int braceDepth = 0;
        PeekableIterator<Token> iterator = new PeekableIterator<>(getTokens());
        List<Token> inStore = new ArrayList<>();
        while (iterator.hasNext()) {
            Token current = iterator.next();
            String v = current.getValue();
            if (current instanceof Operator) {
                if ("<".equals(v)) {
                    ++braceDepth;
                    if (braceDepth == 1) continue;
                } else if (">".equals(v)) {
                    --braceDepth;
                    if (braceDepth == 0) {
                        result.add(inStore.getFirst().getValue());
                        inStore.clear();
                        continue;
                    }
                }
            }

            if (braceDepth == 1 && current instanceof Separator && ",".equals(v)) {
                result.add(inStore.getFirst().getValue());
                inStore.clear();
            } else {
                inStore.add(current);
            }
        }

        return result;
    }
}
