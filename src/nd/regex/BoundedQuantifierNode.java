package nd.regex;

/**
 *
 */
class BoundedQuantifierNode extends QuantifierNode {

    private final int highBound;

    BoundedQuantifierNode(AST term, Token token, int lowBound, int highBound) {
        super(term, token, lowBound);
        if (highBound < lowBound) throw new IllegalArgumentException("Negative high bound less that low bound "
                + highBound + "<" + lowBound);
        this.highBound = highBound;
    }

    int highBound() {
        return highBound;
    }

    @Override
    public <E> E visit(ASTNodeVisitor<E> visitor) {
        return visitor.visit(this);
    }
}
