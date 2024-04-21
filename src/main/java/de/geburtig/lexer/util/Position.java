package de.geburtig.lexer.util;

public record Position(int row, int column) {
    public Position nextColumn() {
        return new Position(row, column + 1);
    }
}
