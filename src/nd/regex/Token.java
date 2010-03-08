package nd.regex;

/**
 *
 */
class Token {

    private final Type type;
    private final String text;

    Token(Type type, String text) {
        this.type = type;
        this.text = text;
    }


    Type type() {return type;}

    String text() {return text;}

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Token)) return false;
        Token otherToken = (Token) other;
        return type.equals(otherToken.type) && text.equals(otherToken.text);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + type.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }


    @Override
    public String toString() {
        return type.name() + " " + text;
    }

    static enum Type {
        EOF,
        CHARACTER,
        LEFT_BRACKET,
        LEFT_BRACKET_CARET,
        RIGHT_BRACKET,
        CARET,
        DOLLAR,
        WORD_BOUNDARY,
        NON_WORD_BOUNDARY,
        ZERO_OR_ONE,
        ZERO_OR_MORE,
        ONE_OR_MORE,
        OR,
        LEFT_CURLY_BRACKET,
        RIGHT_CURLY_BRACKET,
        LEFT_PAREN,
        RIGHT_PAREN,
        CLASS_ANY_CHARACTER,
        CLASS_DIGIT,
        CLASS_NON_DIGIT,
        CLASS_WHITESPACE,
        CLASS_NON_WHITESPACE,
        CLASS_WORD_CHARACTER,
        CLASS_NON_WORD_CHARACTER,

        INTERVAL,
        SEQUENCE
    }
}

