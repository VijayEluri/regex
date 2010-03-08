package nd.regex;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class CharacterClassNode implements AST {

    private final Token token;
    private List<AST> classElements = new ArrayList<AST>();

    CharacterClassNode(Token token) {
        this.token = token;
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
    public <E> E visit(ASTVisitor<E> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <E> E visit(ASTNodeVisitor<E> visitor) {
        return visitor.visit(this);
    }
}
