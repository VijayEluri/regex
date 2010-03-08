package nd.regex;

import java.util.ArrayList;
import java.util.List;

import static nd.regex.Token.Type;

/**
 *
 */
class ParserImpl implements Parser {

    private final Lexer lexer;
    private final List<Token> tokens = new ArrayList<Token>();
    private Token current;
    private int currentIndex = -1;

    ParserImpl(Lexer lexer) {
        this.lexer = lexer;
    }


    @Override
    public AST parse() {
        init();
        AST root = new SequenceNode(new Token(Type.SEQUENCE, ""));
        while (current.type() != Type.EOF) {
            switch (current.type()) {
                case ZERO_OR_ONE:
                case ZERO_OR_MORE:
                case ONE_OR_MORE:
                case LEFT_CURLY_BRACKET: {
                    parseQuantifier(root);
                    break;
                }

                case LEFT_BRACKET:
                case LEFT_BRACKET_CARET: {
                    parseCharacterClass(root);
                    break;
                }

                case CHARACTER:
                case CLASS_ANY_CHARACTER:
                case CLASS_DIGIT:
                case CLASS_NON_DIGIT:
                case CLASS_WHITESPACE:
                case CLASS_NON_WHITESPACE:
                case CLASS_WORD_CHARACTER:
                case CLASS_NON_WORD_CHARACTER: {
                    parseCharacter(root);
                    break;
                }
                default:
                    throw new Error("Unexpected token " + current);
            }
        }
        return root;
    }

    private void parseCharacter(AST currentRoot) {
        currentRoot.addChild(new CharacterNode(current));
        match(Type.CHARACTER);
    }

    private void parseQuantifier(AST currentRoot) {
        if (currentRoot.children().isEmpty()) {
            throw new RuntimeException("Unexpected token " + current);
        }
        AST term = currentRoot.removeLastChild();
        AST quantifier = null;
        if (isPredefinedQuantifier(current.type())) {
            quantifier = parsePredefinedQuantifier(term);
        } else if (isUserDefinedQuantifier(current.type())) {
            quantifier = parseUserDefinedQuantifier(term);
        } else {
            throw new Error("Unexpected token " + current);
        }
        currentRoot.addChild(quantifier);
    }

    private boolean isPredefinedQuantifier(Type t) {
        return t == Type.ZERO_OR_ONE || t == Type.ZERO_OR_MORE || t == Type.ONE_OR_MORE;
    }

    private QuantifierNode parsePredefinedQuantifier(AST term) {
        QuantifierNode quantifier = null;
        if (current.type() == Type.ZERO_OR_MORE) {
            quantifier = new UnboundedQuantifierNode(term, current, 0);
            match(Type.ZERO_OR_MORE);
        } else if (current.type() == Type.ONE_OR_MORE) {
            quantifier = new UnboundedQuantifierNode(term, current, 1);
            match(Type.ONE_OR_MORE);
        } else if (current.type() == Type.ZERO_OR_ONE) {
            quantifier = new BoundedQuantifierNode(term, current, 0, 1);
            match(Type.ZERO_OR_ONE);
        } else {
            throw new Error("Unexpected token " + current);
        }
        return quantifier;
    }

    private boolean isUserDefinedQuantifier(Type t) {
        return t == Type.LEFT_CURLY_BRACKET;
    }

    private QuantifierNode parseUserDefinedQuantifier(AST term) {
        QuantifierNode quantifier = null;
        Token quantifierToken = current;
        match(Type.LEFT_CURLY_BRACKET);
        StringBuilder low = new StringBuilder();
        StringBuilder high = new StringBuilder();
        StringBuilder tmp = low;
        boolean oneBound = true;
        while (current.type() != Type.RIGHT_CURLY_BRACKET) {
            if (!Character.isDigit(current.text().charAt(0)) && !current.text().equals(","))
                throw new Error("Unexpected token " + current);
            if (current.text().equals(",")) {
                tmp = high;
                oneBound = false;
                match(Type.CHARACTER);
            } else {
                tmp.append(current.text());
                match(Type.CHARACTER);
            }
        }
        if (oneBound) {
            quantifier = new BoundedQuantifierNode(term, quantifierToken,
                    Integer.valueOf(low.toString()), Integer.valueOf(low.toString()));
        } else {
            if (high.length() > 0) {
                quantifier = new BoundedQuantifierNode(term, quantifierToken,
                        Integer.valueOf(low.toString()), Integer.valueOf(high.toString()));
            } else {
                quantifier = new UnboundedQuantifierNode(term, quantifierToken, Integer.valueOf(low.toString()));
            }
        }
        match(Type.RIGHT_CURLY_BRACKET);
        return quantifier;
    }

    private void parseCharacterClass(AST currentRoot) {
        AST charClass = new CharacterClassNode(current);
        match(current.type());
        while (current.type() != Type.RIGHT_BRACKET) {
            switch (current.type()) {
                case CHARACTER: {
                    if (lookahead(1).text().equals("-")) {
                        parseCharacterClassInterval(charClass);
                    } else {
                        parseCharacter(charClass);
                    }
                    break;
                }
                case EOF: throw new Error("Unexpected token " + current);
            }
        }
        match(Type.RIGHT_BRACKET);
        currentRoot.addChild(charClass);
    }

    private void parseCharacterClassInterval(AST currentRoot) {
        CharacterNode lowBound = new CharacterNode(current);
        match(Type.CHARACTER);
        match(Type.CHARACTER);
        if (current.type() == Type.RIGHT_BRACKET || current.type() == Type.EOF) {
            throw new Error("Unexpected token " + current);
        } else {
            CharacterNode highBound = new CharacterNode(current);
            match(Type.CHARACTER);
            CharacterClassIntervalNode interval = new CharacterClassIntervalNode(lowBound, highBound);
            currentRoot.addChild(interval);
        }
    }

    private void init() {
        readTokens();
        consume();
    }

    private void readTokens() {
        while (lexer.hasNext()) {
            tokens.add(lexer.nextToken());
        }
    }

    private void consume() {
        currentIndex++;
        if (currentIndex >= tokens.size()) {
            current = new Token(Type.EOF, "EOF");
        } else {
            current = tokens.get(currentIndex);
        }
    }

    private void match(Token.Type type) {
        if (current.type().equals(type)) {
            consume();
        } else {
            throw new Error("expecting " + type + " found " + current.type());
        }
    }

    private Token lookahead(int i) {
        if (currentIndex + i >= tokens.size()) return new Token(Type.EOF, "EOF");
        return tokens.get(currentIndex + i);
    }
}
