package nd.regex;

import static nd.regex.Token.Type;

/**
 *
 */
final class LexerImpl implements Lexer {

    static private final char EOF = (char) -1;
    private final String pattern;
    private int currentIndex = 0;
    private char current;


    LexerImpl(String pattern) {
        this.pattern = pattern;
        this.current = pattern.charAt(0);
    }

    @Override
    public Token nextToken() {
        while (current != EOF) {
            if (isSpecialCharacter(current)) {
                return parseSpecialCharacter();
            } else {
                Token token = new Token(Type.CHARACTER, String.valueOf(current));
                consume();
                return token;
            }
        }
        return new Token(Type.EOF, "EOF");
    }


    private boolean isSpecialCharacter(char c) {
        return c == '[' ||
               c == ']' ||
               c == '^' ||
               c == '.' ||
               c == '?' ||
               c == '*' ||
               c == '+' ||
               c == '\\'||
               c == '{' ||
               c == '}' ||
               c == '(' ||
               c == ')' ||
               c == '$' ||
               c == '-' ||
               c == '|';
    }

    private Token parseSpecialCharacter() {
        switch (current) {
            case '[': {
                consume();
                if (current == '^') {
                    consume();
                    return new Token(Type.LEFT_BRACKET_CARET, "[^");
                } else {
                    return new Token(Type.LEFT_BRACKET, "[");
                }
            }
            case ']': {
                consume(); return new Token(Type.RIGHT_BRACKET, "]");
            }
            case '^': {
                consume(); return new Token(Type.CARET, "^");
            }
            case '$': {
                consume(); return new Token(Type.DOLLAR, "$");
            }
            case '.': {
                consume();
                return new Token(Type.CLASS_ANY_CHARACTER, ".");
            }
            case '?': {
                consume();
                return new Token(Type.ZERO_OR_ONE, "?");
            }
            case '*': {
                consume();
                return new Token(Type.ZERO_OR_MORE, "*");
            }
            case '+': {
                consume();
                return new Token(Type.ONE_OR_MORE, "+");
            }
            case '-': {
                consume();
                return new Token(Type.CHARACTER, "-");
            }
            case '\\': {
                consume();
                return parseSlashCombination();
            }
            case '{': {
                consume();
                return new Token(Type.LEFT_CURLY_BRACKET, "{");
            }
            case '}': {
                consume();
                return new Token(Type.RIGHT_CURLY_BRACKET, "}");
            }
            case '(': {
                consume();
                return new Token(Type.LEFT_PAREN, "(");
            }
            case ')': {
                consume();
                return new Token(Type.RIGHT_PAREN, ")");
            }
            case '|': {
                consume();
                return new Token(Type.OR, "|");
            }
            default:
                throw new Error("Unexpected character " + current);
        }
    }

    private Token parseSlashCombination() {
        if (isSpecialCharacter(current)) {
            Token token = new Token(Type.CHARACTER, String.valueOf(current));
            consume();
            return token;
        } else {
            if (current == 'd') {
                consume();
                return new Token(Type.CLASS_DIGIT, "d");
            } else if (current == 'D') {
                consume();
                return new Token(Type.CLASS_NON_DIGIT, "D");
            } else if (current == 's') {
                consume();
                return new Token(Type.CLASS_WHITESPACE, "s");
            } else if (current == 'S') {
                consume();
                return new Token(Type.CLASS_NON_WHITESPACE, "S");
            } else if (current == 'w') {
                consume();
                return new Token(Type.CLASS_WORD_CHARACTER, "w");
            } else if (current == 'W') {
                consume();
                return new Token(Type.CLASS_NON_WORD_CHARACTER, "W");
            } else if (current == 'b') {
                consume();
                return new Token(Type.WORD_BOUNDARY, "b");
            } else if (current == 'B') {
                consume();
                return new Token(Type.NON_WORD_BOUNDARY, "B");
            }
            throw new RuntimeException("Unexpected character " + current);
        }
    }


    @Override
    public boolean hasNext() {
        return currentIndex < pattern.length();
    }

    private void consume() {
        currentIndex++;
        if (currentIndex >= pattern.length()) {
            current = EOF;
        } else {
            current = pattern.charAt(currentIndex);
        }
    }
}
