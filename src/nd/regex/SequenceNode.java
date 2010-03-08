package nd.regex;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class SequenceNode implements AST {

    private final Token token;
    private final List<AST> children = new ArrayList<AST>();

    SequenceNode(Token token) {
        this.token = token;
    }

    @Override
    public Token token() {
        return token;
    }

    @Override
    public void addChild(AST child) {
        children.add(child);
    }

    @Override
    public List<AST> children() {
        return children;
    }

    @Override
    public AST removeLastChild() {
        return children.remove(children.size() - 1);
    }

    @Override
    public String toString() {
        return String.format("(%s: ...)", token);
    }

    @Override
    public <E> E visit(ASTNodeVisitor<E> visitor) {
        return visitor.visit(this);
    }
}
