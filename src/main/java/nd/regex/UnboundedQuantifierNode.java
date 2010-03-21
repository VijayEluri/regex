package nd.regex;

/**
 * AST node for unbounded quantifier (ex: *, +, {1, }})
 */
final class UnboundedQuantifierNode extends QuantifierNode {

    UnboundedQuantifierNode(AST term, Token token, int lowBound) {
        super(term, token, lowBound);
    }

    @Override
    public <E> E visit(ASTVisitor<E> visitor) {
        return visitor.visit(this);
    }
}
