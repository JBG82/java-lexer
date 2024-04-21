package de.geburtig.lexer;

import de.geburtig.lexer.util.PeekableIterator;
import de.geburtig.lexer.util.Position;
import de.geburtig.lexer.tokens.*;
import lombok.Data;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Lexer {

    public static List<Token> parse(final String content) {
        // Zuerst auf unterster Ebene den Content in Tokens zerlegen
        List<Token> tokenList = initTokenFromContent(content);

        // Dann versuchen Fließkomma-Literale zusammenzuführen (<Literal>.<Literal>, z.B. "634.23"). Hier macht die Gruppierung
        // von Tokens keinen Sinn, da keine Leerzeichen zwischen den Zahlen und dem Punkt erlaubt sind. Im Falle eines Treffers
        // werden die drei Tokens durch ein Literal-Token ersetzt.
        int arrayDepth = 0;
        List<Token> arrayTokens = null;
        PeekableIterator<Token> iterator = new PeekableIterator<>(tokenList);
        while (iterator.hasNext()) {
            Token current = iterator.next();
            if (iterator.elementsLeft() > 1) {
                if (current instanceof Literal t1 && iterator.peekNext() instanceof Separator t2 && iterator.peekNextPlus(1) instanceof Literal t3) {
                    if (t1.getSpaceAfter().isEmpty() && ".".equals(t2.getValue()) && t2.getSpaceAfter().isEmpty()) {
                        current = new Literal(t1.getValue() + t2.getValue() + t3.getValue(), t1.getPosition(), t3.getSpaceAfter());
                        iterator.replace(current);
                        iterator.removeNext(2);
                    }
                }
            }
            if (arrayDepth > 0) {
                arrayTokens.add(current);
                if ("[".equals(current.getValue())) {
                    ++arrayDepth;
                } else if ("]".equals(current.getValue())) {
                    --arrayDepth;
                    if (arrayDepth == 0) {
                        ArrayToken arrayToken = ArrayToken.of(arrayTokens);
                        for (int i = 1; i < arrayTokens.size(); ++i) {
                            iterator.remove();
                        }
                        iterator.replace(arrayToken);
                        arrayTokens = null;
                    }
                }
            } else if ("[".equals(current.getValue())) {
                arrayTokens = new ArrayList<>(List.of(current));
                ++arrayDepth;
            }

        }

        // Danach versuchen sinnhaft zueinander zugehörige Tokens zusammenzuführen:
        //  - Kombination aus UnspecifiedToken und GenericToken zu einem TypeToken zusammengefasst (z.B. "Set<String>")
        //  - Kombination aus UnspecifiedToken/Keyword und Varargs-Operator wird zu einem TypeToken zusammengefasst (z.B. "String...", "int...")
        List<Token> result = new ArrayList<>();
        boolean inAnnotation = false;
        List<Token> annotationTokens = null;
        int annotationDepth = 0;
//        boolean inGeneric = false; // TODO Braucht man den? "genericDepth > 0" tut's doch auch...
        List<Token> genericTokens = null;
        int genericDepth = 0;

        iterator = new PeekableIterator<>(tokenList);
        while (iterator.hasNext()) {
            Token toInsert = iterator.next();
            String value = toInsert.getValue();

            if (toInsert instanceof ArrayToken t) {
                List<Token> tmp = new ArrayList<>(List.of(result.getLast()));
                tmp.addAll(t.getTokens());
                toInsert = ArrayToken.of(tmp);
                if (genericDepth > 0) {
                    genericTokens.removeLast();
                }
                result.removeLast();
            }

            if (genericDepth > 0) {
                genericTokens.add(toInsert);
                if (">".equals(value)) {
                    --genericDepth;
                    if (genericDepth == 0) {
                        // Methode "result.removeFromBack(genericTokens - 1)" wäre hier cool!
                        for (int i = 1; i < genericTokens.size(); ++i) {
                            result.removeLast();
                        }

                        GenericToken genericToken = GenericToken.of(genericTokens);
                        if (!result.isEmpty() && result.getLast() instanceof UnspecifiedToken) {
                            Token last = result.removeLast();
                            result.add(TypeToken.of(List.of(last, genericToken)));
                        } else {
                            result.add(genericToken);
                        }
                        genericTokens = null;
                        continue;
                    }
                }
            } else if (toInsert instanceof Operator o && "...".equals(o.getValue()) && (result.getLast() instanceof UnspecifiedToken || result.getLast() instanceof Keyword)) {
                // Kombination aus UnspecifiedToken/Keyword und Varargs-Operator wird zu einem TypeToken zusammengefasst (z.B. "String...", "int...")
                Token last = result.removeLast();
                result.add(TypeToken.of(List.of(last, toInsert)));
                continue;
            }
            if ("<".equals(value)) {
                ++genericDepth;
                if (genericDepth == 1) {
                    genericTokens = new ArrayList<>(List.of(toInsert));
                }
            } else if (";".equals(value) || "{".equals(value) || (toInsert instanceof Operator o && !o.isGenericOperator())) {
                genericDepth = 0;
                genericTokens = null;
            }

            if (inAnnotation) {
                annotationTokens.add(toInsert);
                if ("(".equals(toInsert.getValue())) {
                    ++annotationDepth;
                    continue;
                } else if (")".equals(toInsert.getValue()) && annotationDepth > 0) {
                    --annotationDepth;
                    if (annotationDepth == 0) {
                        toInsert = AnnotationToken.of(annotationTokens);
                        annotationTokens = null;
                        inAnnotation = false;
                    } else {
                        continue;
                    }
                } else if (annotationDepth > 0 || ".".equals(toInsert.getValue()) || ".".equals(iterator.peekNext().getValue()) || "(".equals(iterator.peekNext().getValue())) {
                    continue;
                } else {
                    toInsert = AnnotationToken.of(annotationTokens);
                    annotationTokens = null;
                    inAnnotation = false;
                }
            } else if ("@".equals(toInsert.getValue())) {
                inAnnotation = true;
                annotationTokens = new ArrayList<>(List.of(toInsert));
                continue;
            } else if ("-".equals(toInsert.getValue()) && iterator.peekNext() instanceof Literal next && iterator.peekPrev() instanceof Operator) {
                toInsert = LiteralToken.of(List.of(toInsert, next));
                iterator.skip(1);
            } else if (iterator.elementsLeft() > 1 && (toInsert instanceof UnspecifiedToken || "this".equals(toInsert.getValue())) && ".".equals(iterator.peekNext().getValue()) && iterator.peekNextPlus(1) instanceof UnspecifiedToken nextPlus) {
                List<Token> tokens = new ArrayList<>(List.of(toInsert, iterator.next(), iterator.next()));
                while (iterator.elementsLeft() > 1 && ".".equals(iterator.peekNext().getValue()) && iterator.peekNextPlus(1) instanceof UnspecifiedToken) {
                    tokens.addAll(List.of(iterator.next(), iterator.next()));
                }
                toInsert = IdentifierGroupToken.of(tokens);
            }
            result.add(toInsert);
        }
        return result;
    }

    static List<Token> initTokenFromContent(final String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        char[] chars = new String(bytes, StandardCharsets.UTF_8).toCharArray();

        State state = new State();
        for (char c : chars) {
            state.updatePosition(c);

            if (state.getStatus() == Status.CATCHING_SINGLE_LINE_COMMENT) {
                if (isEndOfLine(c)) {
                    state.addToken(new SingleLineComment(state.getCaught(), state.getLastStartPosition()));
                    state.setCaught(c);
                    state.setStatus(Status.CATCHING_SPACE);
                } else {
                    state.addToCaught(c);
                }
            } else if (state.getStatus() == Status.CATCHING_MULTI_LINE_COMMENT) {
                if (isEndOfMultiLineComment(state, c)) {
                    state.addToken(new MultiLineComment(state.getCaught() + c, state.getLastStartPosition()));
                    state.clearCaught();
                    state.setStatus(Status.CATCHING_SPACE);
                } else {
                    state.addToCaught(c);
                }
            } else if (state.getStatus() == Status.CATCHING_STRING) {
                if (c == '"' && !isEscaping(state)) {
                    state.addToken(new Literal(state.getCaught() + c, state.getLastStartPosition()));
                    state.clearCaught();
                    state.setStatus(Status.CATCHING_SPACE);
                } else {
                    state.addToCaught(c);
                }
            } else if (state.getStatus() == Status.CATCHING_CHAR) {
                if (c == '\'' && !isEscaping(state)) {
                    state.addToken(new Literal(state.getCaught() + c, state.getLastStartPosition()));
                    state.clearCaught();
                    state.setStatus(Status.CATCHING_SPACE);
                } else {
                    state.addToCaught(c);
                }
            } else if (state.getStatus() == Status.CATCHING_TOKEN) {
                if (c == '"') {
                    addTokenIfSomethingIsCaught(state);
                    state.setCaught(c);
                    state.setStatus(Status.CATCHING_STRING);
                } else if (c == '\'') {
                    addTokenIfSomethingIsCaught(state);
                    state.setCaught(c);
                    state.setStatus(Status.CATCHING_CHAR);
                } else if (isStartOfMultiLineComment(state, c)) {
                    throw new IllegalStateException("Dürfte eigentlich gar nicht passieren!");
                } else if (c == '@') {
                    state.addToken(new AnnotationMarker(state.getCurrentPosition()));
                    state.clearCaught();
                    state.setStatus(Status.CATCHING_SPACE);
                } else if (isSpace(c)) {
                    addTokenIfSomethingIsCaught(state);
                    state.setCaught(c);
                    state.setStatus(Status.CATCHING_SPACE);
                } else if (Operator.isOperatorOrStarting(c)) {
                    addTokenIfSomethingIsCaught(state);
                    state.setCaught(c);
                    state.setStatus(Status.CATCHING_OPERATOR);
                } else if (Separator.isSeparator(c)) {
                    // Funktioniert so nur, solange es keine Trenner mit mehr als einem Zeichen gibt!
                    addTokenIfSomethingIsCaught(state);
                    state.addToken(new Separator(String.valueOf(c), state.getCurrentPosition()));
                    state.clearCaught();
                    state.setStatus(Status.CATCHING_SPACE);
                } else {
                    state.addToCaught(c);
                }
            } else if (state.getStatus() == Status.CATCHING_OPERATOR) {
                if (c == '"') {
                    state.addToken(new Operator(state.getCaught(), state.getLastStartPosition()));
                    state.setCaught(c);
                    state.setStatus(Status.CATCHING_STRING);
                } else if (c == '\'') {
                    state.addToken(new Operator(state.getCaught(), state.getLastStartPosition()));
                    state.setCaught(c);
                    state.setStatus(Status.CATCHING_CHAR);
                } else if (isStartOfSingleLineComment(state, c)) {
                    state.addToCaught(c);
                    state.setStatus(Status.CATCHING_SINGLE_LINE_COMMENT);
                } else if (isStartOfMultiLineComment(state, c)) {
                    state.addToCaught(c);
                    state.setStatus(Status.CATCHING_MULTI_LINE_COMMENT);
                } else if (Operator.isOperator(state.getCaught() + c)) {
                    // ACHTUNG: Funktioniert so nur, solange es keine Operatoren mit mehr als zwei Zeichen gibt!
                    state.addToken(new Operator(state.getCaught() + c, state.getLastStartPosition()));
                    state.clearCaught();
                    state.setStatus(Status.CATCHING_SPACE);
                } else if (Operator.isOnTheWay(state.getCaught() + c)) {
                    state.addToCaught(c);
                    // keep catching operator!?
                } else if (isSpace(c)) {
                    state.addToken(new Operator(state.getCaught(), state.getLastStartPosition()));
                    state.setCaught(c);
                    state.setStatus(Status.CATCHING_SPACE);
                } else if (Separator.isSeparator(state.getCaught())) {
                    // Hier nötig, weil es mind. einen Operator gibt ("..."), der mit einem Separator-Char anfängt!
                    state.addToken(new Separator(state.getCaught(), state.getLastStartPosition()));
                    state.setStatusFromNewCharacter(c);
                } else {
                    state.addToken(new Operator(state.getCaught(), state.getLastStartPosition()));
                    state.setStatusFromNewCharacter(c);
                }
            } else if (state.getStatus() == Status.CATCHING_SPACE) {
                if (isSpace(c)) {
                    state.addToCaught(c);
                } else if (c == '"') {
                    state.getLastToken().setSpaceAfter(state.getCaught());
                    state.setCaught(c);
                    state.setStatus(Status.CATCHING_STRING);
                } else if (c == '\'') {
                    state.getLastToken().setSpaceAfter(state.getCaught());
                    state.setCaught(c);
                    state.setStatus(Status.CATCHING_CHAR);
                } else if (c == '@') {
                    state.getLastToken().setSpaceAfter(state.getCaught());
                    state.addToken(new AnnotationMarker(state.getCurrentPosition()));
                    state.clearCaught();
                    state.setStatus(Status.CATCHING_SPACE); // No change
                } else if (Separator.isSeparator(c)) {
                    state.getLastToken().setSpaceAfter(state.getCaught());
                    // Funktioniert so nur, solange es keine Trenner mit mehr als einem Zeichen gibt!
                    state.addToken(new Separator(String.valueOf(c), state.getCurrentPosition()));
                    state.clearCaught();
                    state.setStatus(Status.CATCHING_SPACE);
                } else {
                    if (!state.getTokenList().isEmpty()) {
                        state.getLastToken().setSpaceAfter(state.getCaught());
                    }
                    state.setStatusFromNewCharacter(c);
                }
            } else {
                throw new IllegalStateException("Wha!?");
            }
        }

        if (state.getStatus() == Status.CATCHING_TOKEN) {
            addTokenIfSomethingIsCaught(state);
        } else if (state.getStatus() == Status.CATCHING_SPACE) {
            state.getLastToken().setSpaceAfter(state.getCaught());
        } else if (state.getStatus() == Status.CATCHING_OPERATOR) {
            state.addToken(new Operator(state.getCaught(), state.getLastStartPosition()));
        } else if (state.getStatus() == Status.CATCHING_SINGLE_LINE_COMMENT) {
            state.addToken(new SingleLineComment(state.getCaught(), state.getLastStartPosition()));
        }

        return state.getTokenList();
    }

    private static void addTokenIfSomethingIsCaught(final State state) {
        if (!state.getCaught().isEmpty()) {
            state.addToken(TokenCreator.from(state.getCaught(), state.getLastStartPosition()));
        }
    }

    private static boolean isEndOfLine(final char c) {
        return c == '\r' || c == '\n';
    }

    private static boolean isStartOfSingleLineComment(final State state, final char c) {
        if (state.getCaught() == null) return false;
        return state.getCaught().equals("/") && c == '/';
    }

    private static boolean isStartOfMultiLineComment(final @NonNull State state, final char c) {
        if (state.getCaught() == null) return false;
        return state.getCaught().equals("/") && c == '*';
    }

    private static boolean isEndOfMultiLineComment(final @NonNull State state, final char c) {
        if (state.getCaught() == null) return false;
        return state.getCaught().endsWith("*") && c == '/';
    }

    private static boolean isSpace(final char c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }

    private static boolean isEscaping(final @NonNull State state) {
        if (state.getCaught() == null) return false;
        return state.getCaught().endsWith("\\");
    }

    @Data
    private static class State {
        /** Aktueller Verarbeitungsstatus */
        private Status status = Status.CATCHING_TOKEN;

        /** Was bisher für das aktuelle Token bereits abgegriffen wurde */
        private String caught = "";

        /** Liste aller bisher erzeugten Token */
        private List<Token> tokenList = new ArrayList<>();

        private Position lastStartPosition = null;
        private Position currentPosition = new Position(1, 0);
        private boolean newLineOnNextUpdate = false;

        public void setCaught(final char c) {
            caught = String.valueOf(c);
            lastStartPosition = currentPosition;
        }
        public void addToCaught(final char c) {
            caught += c;
            if (lastStartPosition == null) {
                lastStartPosition = currentPosition;
            }
        }
        public void clearCaught() {
            caught = "";
            lastStartPosition = null;
        }

        public void addToken(final @NonNull Token token) {
            tokenList.add(token);
        }

        public void setStatusFromNewCharacter(final char c) {
            setCaught(c);
            // TODO: Ggf. nicht vollständig!
            if (Operator.isOperatorOrStarting(c)) {
                status = Status.CATCHING_OPERATOR;
            } else {
                status = Status.CATCHING_TOKEN;
            }
        }

        public @NonNull Token getLastToken() {
            try {
                return tokenList.getLast();
            } catch (NoSuchElementException e) {
                throw e;
            }
        }

        public void updatePosition(final char c) {
            if (newLineOnNextUpdate) {
                currentPosition = new Position(currentPosition.row() + 1, 0);
                newLineOnNextUpdate = false;
            }
            currentPosition = currentPosition.nextColumn();
            if (c == '\n') {
                newLineOnNextUpdate = true;
            }
            if (lastStartPosition == null) {
                lastStartPosition = currentPosition;
            }
        }
    }

    private enum Status {
        CATCHING_TOKEN,
        CATCHING_SPACE,
        CATCHING_OPERATOR,
        CATCHING_STRING,
        CATCHING_CHAR,
        CATCHING_MULTI_LINE_COMMENT,
        CATCHING_SINGLE_LINE_COMMENT
    }
}
