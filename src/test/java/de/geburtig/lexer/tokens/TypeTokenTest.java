package de.geburtig.lexer.tokens;

import de.geburtig.lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class TypeTokenTest {

    @Test
    void testCombinationOfIdentifierGroupTokenAndGenericToken() {
        List<Token> tokens = Lexer.parse("de.geburtig.test.Test<String>");
        assertEquals(1, tokens.size(), tokens.stream().map(Token::getValue).collect(joining(",")));
        TypeToken result = assertInstanceOf(TypeToken.class, tokens.getFirst());
        assertEquals(2, result.getTokens().size());
        assertEquals("de.geburtig.test.Test,<String>", result.getTokens().stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(IdentifierGroupToken.class, result.getTokens().getFirst());
        assertInstanceOf(GenericToken.class, result.getTokens().getLast());
    }

    @Test
    void testCombinationOfUnspecifiedAndGenericToken1() {
        List<Token> tokens = Lexer.parse("List<String>");
        assertEquals(1, tokens.size(), tokens.stream().map(Token::getValue).collect(joining(",")));
        TypeToken result = assertInstanceOf(TypeToken.class, tokens.getFirst());
        assertEquals(2, result.getTokens().size());
        assertEquals("List,<String>", result.getTokens().stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(UnspecifiedToken.class, result.getTokens().getFirst());
        assertInstanceOf(GenericToken.class, result.getTokens().getLast());
    }

    @Test
    void testCombinationOfUnspecifiedAndGenericToken2() {
        List<Token> tokens = Lexer.parse("RatingEnumeration<de.rms.base.foundation.enumeration.dd.DeliveryTyp>");
        assertEquals(1, tokens.size(), tokens.stream().map(Token::getValue).collect(joining(",")));
        TypeToken result = assertInstanceOf(TypeToken.class, tokens.getFirst());
        assertEquals(2, result.getTokens().size());
        assertEquals("RatingEnumeration,<de.rms.base.foundation.enumeration.dd.DeliveryTyp>", result.getTokens().stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(UnspecifiedToken.class, result.getTokens().getFirst());
        assertInstanceOf(GenericToken.class, result.getTokens().getLast());

    }

}