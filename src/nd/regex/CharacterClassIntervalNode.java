package nd.regex;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class CharacterClassIntervalNode implements AST {

    static private final Token token = new Token(Token.Type.INTERVAL, "-");

    private final CharacterNode lowBound;
    private final CharacterNode highBound;
    private final List<AST> children;

    CharacterClassIntervalNode(CharacterNode lowBound, CharacterNode highBound) {
        this.lowBound = lowBound;
        this.highBound = highBound;
        children = new ArrayList<AST>();
        children.add(lowBound);
        children.add(highBound);
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
        return children;
    }

    @Override
    public AST removeLastChild() {
        throw new UnsupportedOperationException();
    }

    CharacterNode lowBound() {
        return lowBound;
    }

    CharacterNode highBound() {
        return highBound;
    }

    @Override
    public <E> E visit(ASTNodeVisitor<E> visitor) {
        return visitor.visit(this);
    }
}
