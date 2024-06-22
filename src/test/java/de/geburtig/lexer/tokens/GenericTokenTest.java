package de.geburtig.lexer.tokens;

import de.geburtig.lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class GenericTokenTest {

    @Test
    void test() {
        List<Token> tokens = Lexer.parse("<S>");
        assertEquals(1, tokens.size());
        GenericToken result = assertInstanceOf(GenericToken.class, tokens.getFirst());
        assertEquals(3, result.getTokens().size());
        assertEquals("<,S,>", result.getTokens().stream().map(Token::getValue).collect(joining(",")));

        List<String> typeNames = result.extractTypeNames();
        assertEquals(1, typeNames.size());
        assertEquals("S", typeNames.getFirst());
    }

    @Test
    void testWithExtends() {
        List<Token> tokens = Lexer.parse("<S extends String>");
        assertEquals(1, tokens.size());
        GenericToken result = assertInstanceOf(GenericToken.class, tokens.getFirst());
        assertEquals(5, result.getTokens().size());
        assertEquals("<,S,extends,String,>", result.getTokens().stream().map(Token::getValue).collect(joining(",")));

        List<String> typeNames = result.extractTypeNames();
        assertEquals(1, typeNames.size());
        assertEquals("S", typeNames.getFirst());
    }

    @Test
    void testMultiple1() {
        List<Token> tokens = Lexer.parse("<S, T>");
        assertEquals(1, tokens.size());
        GenericToken result = assertInstanceOf(GenericToken.class, tokens.getFirst());
        assertEquals(5, result.getTokens().size());
        assertEquals("<,S,,,T,>", result.getTokens().stream().map(Token::getValue).collect(joining(",")));

        List<String> typeNames = result.extractTypeNames();
        assertEquals(2, typeNames.size());
        assertEquals("S", typeNames.getFirst());
        assertEquals("T", typeNames.getLast());
    }

    @Test
    void testMultiple2() {
        List<Token> tokens = Lexer.parse("<S, T extends String>");
        assertEquals(1, tokens.size());
        GenericToken result = assertInstanceOf(GenericToken.class, tokens.getFirst());
        assertEquals(7, result.getTokens().size());
        assertEquals("<,S,,,T,extends,String,>", result.getTokens().stream().map(Token::getValue).collect(joining(",")));

        List<String> typeNames = result.extractTypeNames();
        assertEquals(2, typeNames.size());
        assertEquals("S", typeNames.getFirst());
        assertEquals("T", typeNames.getLast());
    }

    @Test
    void testGenericWrappingIdentifierGroupToken() {
        String value = "<de.rms.base.foundation.enumeration.dd.DeliveryTyp>";
        List<Token> tokens = Lexer.parse(value);
        assertEquals(1, tokens.size());
        assertEquals(value, tokens.getFirst().getValue());
        GenericToken result = assertInstanceOf(GenericToken.class, tokens.getFirst());
        assertEquals(3, result.getTokens().size(), result.getTokens().stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(IdentifierGroupToken.class, result.getTokens().get(1));

        List<String> typeNames = result.extractTypeNames();
        assertEquals(1, typeNames.size());
        assertEquals("de.rms.base.foundation.enumeration.dd.DeliveryTyp", typeNames.getFirst());
    }

}