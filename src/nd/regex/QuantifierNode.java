package nd.regex;

import java.util.Collections;
import java.util.List;

/**
 *
 */
abstract class QuantifierNode implements AST {

    private final Token token;
    private final int lowBound;
    private final AST term;

    QuantifierNode(AST term, Token token, int lowBound) {
        if (lowBound < 0) throw new IllegalArgumentException("Negative low bound " + lowBound);
        this.term = term;
        this.token = token;
        this.lowBound = lowBound;
    }

    AST term() {
        return term;
    }

    int lowBound() {
        return lowBound;
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
        return Collections.singletonList(term);
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
