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
        AST root = new AST(new Token(Type.SEQUENCE, ""));
        while (current.type() != Type.EOF) {
            switch (current.type()) {
                case ZERO_OR_ONE:
                case ZERO_OR_MORE:
                case ONE_OR_MORE: {
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
        currentRoot.addChild(new AST(current));
        match(current.type());
    }

    private void parseQuantifier(AST currentRoot) {
        if (currentRoot.children().isEmpty()) {
            throw new RuntimeException("Unexpected token " + current);
        }
        AST last = currentRoot.removeLastChild();
        AST quantifier = new AST(current);
        quantifier.addChild(last);
        currentRoot.addChild(quantifier);
        match(current.type());
    }

    private void parseCharacterClass(AST currentRoot) {
        AST charClass = new AST(current);
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
                case EOF:
                    throw new Error("Unexpected token " + current);
            }
        }
        match(Type.RIGHT_BRACKET);
        currentRoot.addChild(charClass);
    }

    private void parseCharacterClassInterval(AST currentRoot) {
        AST intervalStart = new AST(current);
        match(Type.CHARACTER);
        AST interval = new AST(new Token(Type.INTERVAL, "-"));
        match(Type.CHARACTER);
        if (current.type() == Type.RIGHT_BRACKET || current.type() == Type.EOF) {
            throw new Error("Unexpected token " + current);
        } else {
            AST intervalEnd = new AST(current);
            match(Type.CHARACTER);
            interval.addChild(intervalStart);
            interval.addChild(intervalEnd);
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
