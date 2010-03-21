package nd.regex;

import java.util.Collections;
import java.util.List;

/**
 *
 */
final class AnchorNode implements AST {

    private final Token token;

    AnchorNode(Token token) {
        this.token = token;
    }

    @Override
    public Token token() {
        return token;
    }

    @Override
    public void addChild(AST child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AST> children() {
        return Collections.emptyList();
    }

    @Override
    public AST removeLastChild() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> E visit(ASTVisitor<E> visitor) {
        return visitor.visit(this);
    }
}
