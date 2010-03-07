package nd.regex;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class AST {

    private final Token token;
    private final List<AST> children = new ArrayList<AST>();

    AST(Token token) {
        this.token = token;
    }

    Token token() {
        return token;
    }

    void addChild(AST child) {
        children.add(child);
    }

    List<AST> children() {
        return children;
    }

    AST removeLastChild() {
        return children.remove(children.size() - 1);
    }

    @Override
    public String toString() {
        return String.format("(%s: ...)", token);
    }

    <E> E visit(ASTVisitor<E> visitor) {
        return visitor.visit(this);
    }

}
