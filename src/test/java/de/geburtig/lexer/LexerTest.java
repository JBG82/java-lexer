package de.geburtig.lexer;

import de.geburtig.lexer.util.Position;
import de.geburtig.lexer.tokens.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @Test
    void testAssignment1() {
        List<Token> result = Lexer.parse("int x = 10;");
        assertEquals(5, result.size());
        assertEquals("int,x,=,10,;", result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals(new Keyword("int", new Position(1, 1), " "), result.get(0));
        // TODO: Should be Identifier!
        assertEquals(new UnspecifiedToken("x", new Position(1, 5), " "), result.get(1));
        assertEquals(new Operator("=", new Position(1, 7), " "), result.get(2));
        assertEquals(new Literal("10", new Position(1, 9), ""), result.get(3));
        assertEquals(new Separator(";", new Position(1, 11), ""), result.get(4));
    }

    @Test
    void testAssignment2() {
        double x = 345.34;
        List<Token> result = Lexer.parse("int x=-123;");
        assertEquals(5, result.size());
        assertEquals("int,x,=,-123,;", result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals(new Keyword("int", new Position(1, 1), " "), result.get(0));
        assertEquals(new UnspecifiedToken("x", new Position(1, 5), ""), result.get(1));
        assertEquals(new Operator("=", new Position(1, 6), ""), result.get(2));
        assertInstanceOf(LiteralToken.class, result.get(3));
        assertEquals("-123", result.get(3).getValue());
        assertEquals(new Separator(";", new Position(1, 11), ""), result.get(4));
    }

    @Test
    void testAssignment3() {
        List<Token> result = Lexer.parse("x=12.33");
        assertEquals(3, result.size());
        assertEquals("x,=,12.33", result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals(new UnspecifiedToken("x", new Position(1, 1), ""), result.get(0));
        assertEquals(new Operator("=", new Position(1, 2), ""), result.get(1));
        assertEquals(new Literal("12.33", new Position(1, 3), ""), result.get(2));
    }

    @Test
    void testAssignment4() {
        List<Token> result = Lexer.parse("java.lang.String test = \"\"");
        assertEquals(4, result.size());
        assertEquals("java.lang.String,test,=,\"\"", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(IdentifierGroupToken.class, result.get(0));
        assertEquals(new UnspecifiedToken("test", new Position(1, 18), " "), result.get(1));
        assertEquals(new Operator("=", new Position(1, 23), " "), result.get(2));
        assertEquals(new Literal("\"\"", new Position(1, 25), ""), result.get(3));
    }

    @Test
    void testAssignment5() {
        List<Token> result = Lexer.parse("this.test = \"\"");
        assertEquals(3, result.size());
        assertEquals("this.test,=,\"\"", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(IdentifierGroupToken.class, result.get(0));
        assertEquals(new Operator("=", new Position(1, 11), " "), result.get(1));
        assertEquals(new Literal("\"\"", new Position(1, 13), ""), result.get(2));
    }

    @Test
    void testAssignment6() {
        List<Token> result = Lexer.parse("private final String[] test = new String[] { \"blah\", \"blubb\" };");
        result.forEach(System.out::println);
        assertEquals(13, result.size());
        assertEquals("private,final,String[],test,=,new,String[],{,\"blah\",,,\"blubb\",},;", result.stream().map(Token::getValue).collect(joining(",")));
    }

    @Test
    void testAnnotation1() {
        List<Token> result = Lexer.parse("private @NonNull String value");
        assertEquals(4, result.size());
        assertEquals("private,@NonNull,String,value", result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals(new Keyword("private", new Position(1, 1), " "), result.get(0));
        Token annotationToken = result.get(1);
        assertInstanceOf(AnnotationToken.class, annotationToken);
        assertEquals("@NonNull", annotationToken.getValue());
        assertEquals("AnnotationToken", annotationToken.getType());
        assertEquals(new Position(1, 9), annotationToken.getPosition());
        assertEquals(" ", annotationToken.getSpaceAfter());
        assertEquals(new UnspecifiedToken("String", new Position(1, 18), " "), result.get(2));
        assertEquals(new UnspecifiedToken("value", new Position(1, 25), ""), result.get(3));
    }

    @Test
    void testAnnotation2() {
        List<Token> result = Lexer.parse("public @NonNull @Getter float x");
        assertEquals(5, result.size());
        assertEquals("public,@NonNull,@Getter,float,x", result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals(new Keyword("public", new Position(1, 1), " "), result.get(0));
        Token annotationToken = result.get(1);
        assertInstanceOf(AnnotationToken.class, annotationToken);
        assertEquals("@NonNull", annotationToken.getValue());
        assertEquals(new Position(1, 8), annotationToken.getPosition());
        assertEquals(" ", annotationToken.getSpaceAfter());
        annotationToken = result.get(2);
        assertInstanceOf(AnnotationToken.class, annotationToken);
        assertEquals("@Getter", annotationToken.getValue());
        assertEquals(new Position(1, 17), annotationToken.getPosition());
        assertEquals(" ", annotationToken.getSpaceAfter());
        assertEquals(new Keyword("float", new Position(1, 25), " "), result.get(3));
        assertEquals(new UnspecifiedToken("x", new Position(1, 31), ""), result.get(4));
    }

    @Test
    void testAnnotation3() {
        List<Token> result = Lexer.parse("final @lombok.NonNull boolean b");
        assertEquals(4, result.size());
        assertEquals("final,@lombok.NonNull,boolean,b", result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals(new Keyword("final", new Position(1, 1), " "), result.get(0));
        Token annotationToken = result.get(1);
        assertInstanceOf(AnnotationToken.class, annotationToken);
        assertEquals("@lombok.NonNull", annotationToken.getValue());
        assertEquals(new Position(1, 7), annotationToken.getPosition());
        assertEquals(" ", annotationToken.getSpaceAfter());
        assertEquals(new Keyword("boolean", new Position(1, 23), " "), result.get(2));
        assertEquals(new UnspecifiedToken("b", new Position(1, 31), ""), result.get(3));
    }

    @Test
    void testAnnotation4() {
        List<Token> result = Lexer.parse("final @ lombok  .   NonNull boolean b");
        assertEquals(4, result.size());
        assertEquals("final,@ lombok  .   NonNull,boolean,b", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(AnnotationToken.class, result.get(1));
        assertEquals("@lombok.NonNull", ((AnnotationToken) result.get(1)).getTokens().stream().map(Token::getValue).collect(joining()));
    }

    @Test
    void testAnnotation5() {
        List<Token> result = Lexer.parse("final @Getter(value = AccessLevel.PUBLIC) boolean b");
        assertEquals(4, result.size());
        assertEquals("final,@Getter(value = AccessLevel.PUBLIC),boolean,b", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(AnnotationToken.class, result.get(1));
    }

    @Test
    void testAnnotation6() {
        List<Token> result = Lexer.parse("@Tags(value = {@Tag(\"Blah\"), @Tag(\"Blubb\"), @Tag(\"Hutzl\")}) void test() {}");
        assertEquals(7, result.size());
        assertEquals("@Tags(value = {@Tag(\"Blah\"), @Tag(\"Blubb\"), @Tag(\"Hutzl\")}),void,test,(,),{,}", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(AnnotationToken.class, result.get(0));
    }

    @Test
    void testGenericToken1() {
        List<Token> result = Lexer.parse("<T>");
        assertEquals(1, result.size());
        GenericToken token = assertInstanceOf(GenericToken.class, result.get(0));
        assertEquals(3, token.getTokens().size());
        assertEquals("<,T,>", token.getTokens().stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(UnspecifiedToken.class, token.getTokens().get(1));
        assertEquals(1, token.getContentTokens().size());
        assertEquals("T", token.getContentTokens().get(0).getValue());
        assertEquals("of type T", token.toJavadocPart());
    }

    @Test
    void testGenericToken2() {
        List<Token> result = Lexer.parse("<T, X>");
        assertEquals(1, result.size());
        GenericToken token = assertInstanceOf(GenericToken.class, result.get(0));
        assertEquals(5, token.getTokens().size());
        assertEquals("<,T,,,X,>", token.getTokens().stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(UnspecifiedToken.class, token.getTokens().get(1));
        assertInstanceOf(Separator.class, token.getTokens().get(2));
        assertInstanceOf(UnspecifiedToken.class, token.getTokens().get(3));
        assertEquals(2, token.getContentTokens().size());
        assertEquals("T", token.getContentTokens().get(0).getValue());
        assertEquals("X", token.getContentTokens().get(1).getValue());
        assertEquals("with key type T and value type X", token.toJavadocPart());
    }

    @Test
    void testGenericToken3() {
        List<Token> result = Lexer.parse("<T, X,Y,Z>");
        assertEquals(1, result.size());
        GenericToken token = assertInstanceOf(GenericToken.class, result.get(0));
        assertEquals(9, token.getTokens().size());
        assertEquals(4, token.getContentTokens().size());
        assertEquals("T,X,Y,Z", token.getContentTokens().stream().map(Token::getValue).collect(joining(",")));
        assertEquals("of types T, X, Y, Z", token.toJavadocPart());
    }

    @Test
    void testGenericToken4() {
        List<Token> result = Lexer.parse("<String, List<Integer>>");
        assertEquals(1, result.size());
        GenericToken token = assertInstanceOf(GenericToken.class, result.get(0));
        assertEquals(8, token.getTokens().size());
        assertEquals(2, token.getContentTokens().size());
        assertEquals("String,List<Integer>", token.getContentTokens().stream().map(Token::getValue).collect(joining(",")));
        assertEquals("with key type String and value type List<Integer>", token.toJavadocPart());
    }

    @Test
    void testGenerics1() {
        final Set  <   String   > set;

        List<Token> result = Lexer.parse("final Set<String> set;");
        result.forEach(System.out::println);
        assertEquals(4, result.size());
        assertEquals("final,Set<String>,set,;", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(TypeToken.class, result.get(1));
        assertEquals("Set of type String", ((TypeToken) result.get(1)).toJavadocOutput());
        assertInstanceOf(UnspecifiedToken.class, result.get(2));
    }

    @Test
    void testGenerics2() {
        List<Token> result = Lexer.parse("public <T> Set<T> of()");
        assertEquals(6, result.size());
        assertEquals("public,<T>,Set<T>,of,(,)", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(GenericToken.class, result.get(1));
        assertInstanceOf(TypeToken.class, result.get(2));
        assertEquals("Set of type T", ((TypeToken) result.get(2)).toJavadocOutput());
    }

    @Test
    void testGenerics3() {
        List<Token> result = Lexer.parse("Map<String, List<Integer>> map");
        assertEquals(2, result.size());
        assertEquals("Map<String, List<Integer>>,map", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(TypeToken.class, result.get(0));
        assertEquals("Map with key type String and value type List<Integer>", ((TypeToken) result.get(0)).toJavadocOutput());
        assertInstanceOf(UnspecifiedToken.class, result.get(1));
    }

    @Test
    void testGenerics4() {
        String s = "var t = x < 10 && x > 0;";
//        String s = "if (this.lower != null && subject.compareTo(this.lower) < 0 || this.upper != null && subject.compareTo(this.upper) > 0) {";
        List<Token> result = Lexer.parse(s);
        assertEquals(11, result.size());
        assertEquals("var,t,=,x,<,10,&&,x,>,0,;", result.stream().map(Token::getValue).collect(joining(",")));
    }

    @Test
    void testGenerics5() {
        String s = "var t = x < y && x > z;";
        List<Token> result = Lexer.parse(s);
        assertEquals(11, result.size(), result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals("var,t,=,x,<,y,&&,x,>,z,;", result.stream().map(Token::getValue).collect(joining(",")));
    }

    @Test
    void testOperators1() {
        List<Token> result = Lexer.parse("boolean x = t1 < t2;");
        assertEquals(7, result.size());
        assertEquals("boolean,x,=,t1,<,t2,;", result.stream().map(Token::getValue).collect(joining(",")));
    }

    @Test
    void testArrays() {
        // Java-Style array declaration
        List<Token> result = Lexer.parse("int[] x;");
        assertEquals(3, result.size());
        assertEquals("int[],x,;", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(ArrayToken.class, result.get(0));
        assertInstanceOf(UnspecifiedToken.class, result.get(1));
        assertInstanceOf(Separator.class, result.get(2));
    }

    @Test
    void testArrays1() {
        // Java-Style array declaration
        List<Token> result = Lexer.parse("int[] x = new int[5];");
        assertEquals(6, result.size());
        assertEquals("int[],x,=,new,int[5],;", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(ArrayToken.class, result.get(0));
        assertInstanceOf(ArrayToken.class, result.get(4));
    }

    @Test
    void testArrays2() {
        // C-Style array declaration
        List<Token> result = Lexer.parse("int y[] = new int[5];");
        assertEquals(6, result.size());
        assertEquals("int,y[],=,new,int[5],;", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(ArrayToken.class, result.get(1));
        assertInstanceOf(ArrayToken.class, result.get(4));
    }

    @Test
    void testArrays3() {
        List<Token> result = Lexer.parse("x[0] = 5;");
        assertEquals(4, result.size());
        assertEquals("x[0],=,5,;", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(ArrayToken.class, result.get(0));
    }

    @Test
    void testArrays4() {
        List<Token> result = Lexer.parse("List<Integer[]> test;");
        assertEquals(3, result.size());
        assertEquals("List<Integer[]>,test,;", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(TypeToken.class, result.get(0));
        assertEquals("List of type Integer[]", ((TypeToken) result.get(0)).toJavadocOutput());
        assertInstanceOf(GenericToken.class, ((GroupToken) result.get(0)).getTokens().get(1));
        assertInstanceOf(ArrayToken.class, ((GroupToken) ((GroupToken) result.get(0)).getTokens().get(1)).getTokens().get(1));
    }

    @Test
    void testArrays5() {
        List<Token> result = Lexer.parse("x[y[3]] = 5;");
        assertEquals(4, result.size());
        assertEquals("x[y[3]],=,5,;", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(ArrayToken.class, result.get(0));
    }

    @Test
    void testArrays6() {
        List<Token> result = Lexer.parse("x[(int)(0.4 - 0.4)] = 3;");
        assertEquals(4, result.size(), result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals("x[(int)(0.4 - 0.4)],=,3,;", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(ArrayToken.class, result.get(0));
        Token literal1 = ((GroupToken) result.get(0)).getTokens().get(6);
        assertEquals(new Literal("0.4", new Position(1, 9)), literal1);
        Token literal2 = ((GroupToken) result.get(0)).getTokens().get(8);
        assertEquals(new Literal("0.4", new Position(1, 15)), literal2);
    }

    @Test
    void testArrays7() {
        List<Token> result = Lexer.parse("java.lang.String[] blah");
        assertEquals(2, result.size());
        assertEquals("java.lang.String[],blah", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(ArrayToken.class, result.get(0));
        assertInstanceOf(IdentifierGroupToken.class, ((GroupToken) result.get(0)).getTokens().get(0));
    }

    @Test
    void testArrays8() {
        List<Token> result = Lexer.parse("int[] tt = new int[java.lang.Integer.valueOf(\"5\")]");
        assertEquals(5, result.size());
        assertEquals("int[],tt,=,new,int[java.lang.Integer.valueOf(\"5\")]", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(ArrayToken.class, result.get(0));
        assertInstanceOf(ArrayToken.class, result.get(4));
    }

    @Test
    void test1() {
        List<Token> result = Lexer.parse("int x = 27 + 4;");
        assertEquals(7, result.size());
        assertEquals("int,x,=,27,+,4,;", result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals(new Keyword("int", new Position(1, 1), " "), result.get(0));
        assertEquals(new UnspecifiedToken("x", new Position(1, 5), " "), result.get(1));
        assertEquals(new Operator("=", new Position(1, 7), " "), result.get(2));
        assertEquals(new Literal("27", new Position(1, 9), " "), result.get(3));
        assertEquals(new Operator("+", new Position(1, 12), " "), result.get(4));
        assertEquals(new Literal("4", new Position(1, 14), ""), result.get(5));
        assertEquals(new Separator(";", new Position(1, 15), ""), result.get(6));
    }

    @Test
    void test2() {
        List<Token> result = Lexer.parse("int x=27+4;");
        assertEquals(7, result.size());
        assertEquals("int,x,=,27,+,4,;", result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals(new Keyword("int", new Position(1, 1), " "), result.get(0));
        assertEquals(new UnspecifiedToken("x", new Position(1, 5), ""), result.get(1));
        assertEquals(new Operator("=", new Position(1, 6), ""), result.get(2));
        assertEquals(new Literal("27", new Position(1, 7), ""), result.get(3));
        assertEquals(new Operator("+", new Position(1, 9), ""), result.get(4));
        assertEquals(new Literal("4", new Position(1, 10), ""), result.get(5));
        assertEquals(new Separator(";", new Position(1, 11), ""), result.get(6));
    }

    @Test
    void test3() {
        List<Token> result = Lexer.parse("boolean check=test<=8;");
        assertEquals(7, result.size());
        assertEquals("boolean,check,=,test,<=,8,;", result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals(new Keyword("boolean", new Position(1, 1), " "), result.get(0));
        assertEquals(new UnspecifiedToken("check", new Position(1, 9), ""), result.get(1));
        assertEquals(new Operator("=", new Position(1, 14), ""), result.get(2));
        assertEquals(new UnspecifiedToken("test", new Position(1, 15), ""), result.get(3));
        assertEquals(new Operator("<=", new Position(1, 19), ""), result.get(4));
        assertEquals(new Literal("8", new Position(1, 21), ""), result.get(5));
        assertEquals(new Separator(";", new Position(1, 22), ""), result.get(6));
    }

    @Test
    void test4() {
        List<Token> result = Lexer.parse("List<String> t = List.of(\"a\", \"b\", \"c\");");
        assertEquals(12, result.size());
        assertEquals("List<String>,t,=,List.of,(,\"a\",,,\"b\",,,\"c\",),;", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(TypeToken.class, result.get(0));
        assertEquals("List of type String", ((TypeToken) result.get(0)).toJavadocOutput());
        // TODO: "List.of" wurde als IdentifierGroupToken zusammengeführt, ist das an dieser Stelle sinnvoll?
        assertInstanceOf(IdentifierGroupToken.class, result.get(3));
    }

    @Test
    void test5() {
        List<Token> result = Lexer.parse("String x = \"5 + 8 = 13\";");
        assertEquals(5, result.size());
        assertEquals("String,x,=,\"5 + 8 = 13\",;", result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals(new UnspecifiedToken("String", new Position(1, 1), " "), result.get(0));
        assertEquals(new UnspecifiedToken("x", new Position(1, 8), " "), result.get(1));
        assertEquals(new Operator("=", new Position(1, 10), " "), result.get(2));
        assertEquals(new Literal("\"5 + 8 = 13\"", new Position(1, 12), ""), result.get(3));
        assertEquals(new Separator(";", new Position(1, 24), ""), result.get(4));
    }

    @Test
    void test6() {
        List<Token> result = Lexer.parse("String x = \"\\\"a\\\" + \\\"b\\\" = \\\"ab\\\"\";");
        assertEquals(5, result.size());
        assertEquals("String,x,=,\"\\\"a\\\" + \\\"b\\\" = \\\"ab\\\"\",;", result.stream().map(Token::getValue).collect(joining(",")));
    }

    @Test
    void test7() {
        List<Token> result = Lexer.parse("int x = 5 /*Kommentar x=\"Blah\", y=5+8*/;");
        assertEquals(6, result.size());
        assertEquals("int,x,=,5,/*Kommentar x=\"Blah\", y=5+8*/,;", result.stream().map(Token::getValue).collect(joining(",")));
    }

    @Test
    void test8() {
        List<Token> result = Lexer.parse("int x=\"blah\"");
        assertEquals(4, result.size());
        assertEquals("int,x,=,\"blah\"", result.stream().map(Token::getValue).collect(joining(",")));
    }

    @Test
    void testSingleLineComment1() {
        String content = """
            x = 5;
            y = 7; // "Testkommentar"
            s = x+y;
            """;
        List<Token> result = Lexer.parse(content);
        assertEquals(15, result.size());
        assertEquals("x,=,5,;,y,=,7,;,// \"Testkommentar\",s,=,x,+,y,;", result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals(new UnspecifiedToken("x", new Position(1, 1), " "), result.get(0));
        assertEquals(new Operator("=", new Position(1, 3), " "), result.get(1));
        assertEquals(new Literal("5", new Position(1, 5), ""), result.get(2));
        assertEquals(new Separator(";", new Position(1, 6), "\n"), result.get(3));
        assertEquals(new UnspecifiedToken("y", new Position(2, 1), " "), result.get(4));
        assertEquals(new Operator("=", new Position(2, 3), " "), result.get(5));
        assertEquals(new Literal("7", new Position(2, 5), ""), result.get(6));
        assertEquals(new Separator(";", new Position(2, 6), " "), result.get(7));
        assertEquals(new SingleLineComment("// \"Testkommentar\"", new Position(2, 8), "\n"), result.get(8));
        assertEquals(new UnspecifiedToken("s", new Position(3, 1), " "), result.get(9));
        assertEquals(new Operator("=", new Position(3, 3), " "), result.get(10));
        assertEquals(new UnspecifiedToken("x", new Position(3, 5), ""), result.get(11));
        assertEquals(new Operator("+", new Position(3, 6), ""), result.get(12));
        assertEquals(new UnspecifiedToken("y", new Position(3, 7), ""), result.get(13));
        assertEquals(new Separator(";", new Position(3, 8), "\n"), result.get(14));
    }

    @Test
    void testStringAsInnerParameter() {
        List<Token> result = Lexer.parse("MediaType x = TEXT_HTML.deriveParameters(new Pair<String, String>(\"charset\", \"Blubb\"));");
        assertEquals(14, result.size());
        assertEquals("MediaType,x,=,TEXT_HTML.deriveParameters,(,new,Pair<String, String>,(,\"charset\",,,\"Blubb\",),),;", result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals(new UnspecifiedToken("MediaType", new Position(1, 1), " "), result.get(0));
        assertEquals(new UnspecifiedToken("x", new Position(1, 11), " "), result.get(1));
        assertEquals(new Operator("=", new Position(1, 13), " "), result.get(2));
        assertInstanceOf(IdentifierGroupToken.class, result.get(3));
        assertEquals("TEXT_HTML.deriveParameters", result.get(3).getValue());
        assertEquals(new Separator("(", new Position(1, 41), ""), result.get(4));
        assertEquals(new Keyword("new", new Position(1, 42), " "), result.get(5));
        assertInstanceOf(TypeToken.class, result.get(6));
        assertEquals("Pair<String, String>", result.get(6).getValue());
        assertEquals(new Separator("(", new Position(1, 66), ""), result.get(7));
        assertEquals(new Literal("\"charset\"", new Position(1, 67), ""), result.get(8));
        assertEquals(new Separator(",", new Position(1, 76), ""), result.get(9));
        assertEquals(new Literal("\"Blubb\"", new Position(1, 78), ""), result.get(10));
    }

    @Test
    void testVarargsOperator1() {
        List<Token> result = Lexer.parse("String... test");
        result.forEach(System.out::println);
        // TODO: Der UnspecifiedToken ("String") muss noch mit dem Varargs-Operator ("...") zu einem TypeToken vereinigt werden
        assertEquals(2, result.size(), result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals("String...,test", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(TypeToken.class, result.getFirst());
    }

    @Test
    void testVarargsOperator2() {
        List<Token> result = Lexer.parse("void test(String... param);");
        result.forEach(System.out::println);
        assertEquals(7, result.size(), result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals("void,test,(,String...,param,),;", result.stream().map(Token::getValue).collect(joining(",")));
    }

    @Test
    void testChar() {
        List<Token> result = Lexer.parse("char c = 'x';");
        assertEquals(5, result.size(), result.stream().map(Token::getValue).collect(joining(",")));
        assertEquals("char,c,=,'x',;", result.stream().map(Token::getValue).collect(joining(",")));
        assertInstanceOf(Literal.class, result.get(3));
    }
}