package nd.regex;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
final class CharacterClassNode implements AST {

    private final Token token;
    private final List<AST> classElements = new ArrayList<AST>();
    private final boolean exclusive;

    CharacterClassNode(Token token) {
        this.token = token;
        exclusive = token.type() == Token.Type.LEFT_BRACKET_CARET;
    }

    boolean exclusive() {
        return exclusive;
    }

    @Override
    public Token token() {
        return token;
    }

    @Override
    public void addChild(AST child) {
        classElements.add(child);
    }

    @Override
    public List<AST> children() {
        return classElements;
    }

    @Override
    public AST removeLastChild() {
        return classElements.remove(classElements.size() - 1);
    }

    @Override
    public <E> E visit(ASTNodeVisitor<E> visitor) {
        return visitor.visit(this);
    }
}
