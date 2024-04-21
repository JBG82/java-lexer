package de.geburtig.lexer.tokens;

import de.geburtig.parser.model.Position;

import java.util.Set;

public class Keyword extends Token {

    // TODO: Liste bezieht sich auf Java 11
    public static final Set<String> KEYWORDS = Set.of(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if",
            "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private",
            "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
            "throw", "throws", "transient", "try", "var", "void", "volatile", "while"
    );

    public Keyword(final String value, final Position position) {
        super(value, position);
    }

    public Keyword(final String value, final Position position, final String spaceAfter) {
        super(value, position, spaceAfter);
    }

    public boolean isVisibility() {
        return Set.of("public", "protected", "private").contains(getValue());
    }

    public boolean isModifier() {
        return Set.of("static", "final", "volatile", "synchronized").contains(getValue());
    }

    public boolean isPrimitiveType() {
        return Set.of("int", "long", "byte", "char", "double", "float", "boolean").contains(getValue());
    }
}
