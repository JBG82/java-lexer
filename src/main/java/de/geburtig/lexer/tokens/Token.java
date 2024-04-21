package de.geburtig.lexer.tokens;

import de.geburtig.parser.model.Position;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Token is the smallest element in a source code, e.g. an identifier, literal, operator etc.
 */
@Data
@EqualsAndHashCode(of = {"type","position","value"})
public abstract class Token {

    /** Der textuelle Wert es Tokens im Quellcode */
    private final String value;

    /** Die Position des Tokens im Quellcode */
    private final Position position;

    private final String type;

    /** Der hinter dem Token befindliche Leerraum (Leerzeichen, Tabs, Zeilenumbrüche etc.) */
    private String spaceAfter;

    public Token(final String value, final Position position) {
        this(value, position, "");
    }

    public Token(final String value, final Position position, final String spaceAfter) {
        this.value = value;
        this.position = position;
        this.spaceAfter = spaceAfter;
        this.type = getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return String.format("%s(value=%s, position=%s, spaceAfter=\"%s\")", type, value, position, spaceAfter);
    }
}
