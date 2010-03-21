package nd.regex;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for alternative (|)
 */
final class AlternativeNode implements AST {

    private final static Token token = new Token(Token.Type.OR, "|");
    private final List<AST> firstAlternative;
    private List<AST> secondAlternative = new ArrayList<AST>();

    AlternativeNode(List<AST> firstAlternative) {
        this.firstAlternative = firstAlternative;
    }

    List<AST> getFirstAlternative() {
        return firstAlternative;
    }

    List<AST> getSecondAlternative() {
        return secondAlternative;
    }

    @Override
    public Token token() {
        return token;
    }

    @Override
    public void addChild(AST child) {
        secondAlternative.add(child);
    }

    @Override
    public List<AST> children() {
        return secondAlternative;
    }

    @Override
    public AST removeLastChild() {
        return secondAlternative.remove(secondAlternative.size() - 1);
    }

    @Override
    public <E> E visit(ASTVisitor<E> visitor) {
        return visitor.visit(this);
    }

}
