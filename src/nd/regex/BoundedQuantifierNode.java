package nd.regex;

/**
 *
 */
final class BoundedQuantifierNode extends QuantifierNode {

    private final int highBound;

    BoundedQuantifierNode(AST term, Token token, int lowBound, int highBound) {
        super(term, token, lowBound);
        if (highBound < lowBound)
            throw new IllegalArgumentException(String.format("Negative high bound less that low bound: %s < %s",
                    highBound, lowBound));
        this.highBound = highBound;
    }

    int highBound() {
        return highBound;
    }

    @Override
    public <E> E visit(ASTVisitor<E> visitor) {
        return visitor.visit(this);
    }
}
