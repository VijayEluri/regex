package nd.regex;

/**
 *
 */
public class PrintVisitor2 implements ASTNodeVisitor {

    @Override
    public Object visit(SequenceNode sequence) {
        for (AST child : sequence.children()) {
            child.visit(this);
        }
        return null;
    }

    @Override
    public Object visit(BoundedQuantifierNode quantifier) {
        quantifier.term().visit(this);
        System.out.print(String.format("{%d,%d}", quantifier.lowBound(), quantifier.highBound()));
        return null;
    }

    @Override
    public Object visit(UnboundedQuantifierNode quantifier) {
        quantifier.term().visit(this);
        System.out.print(String.format("{%d,}", quantifier.lowBound()));
        return null;
    }

    @Override
    public Object visit(CharacterClassNode charClass) {
        System.out.print(charClass.token().text());
        for (AST child : charClass.children()) {
            child.visit(this);
        }
        System.out.print("]");
        return null;
    }

    @Override
    public Object visit(CharacterClassIntervalNode interval) {
        interval.lowBound().visit(this);
        System.out.print("-");
        interval.highBound().visit(this);
        return null;
    }

    @Override
    public Object visit(CharacterNode character) {
        System.out.print(character.token().text());
        return null;
    }
}
