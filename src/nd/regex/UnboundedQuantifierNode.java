package nd.regex;

/**
 *
 */
final class UnboundedQuantifierNode extends QuantifierNode {

    UnboundedQuantifierNode(AST term, Token token, int lowBound) {
        super(term, token, lowBound);
    }

    @Override
    public <E> E visit(ASTNodeVisitor<E> visitor) {
        return visitor.visit(this);
    }
}
