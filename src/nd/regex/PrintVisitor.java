package nd.regex;

/**
 *
 */
public class PrintVisitor implements ASTVisitor<Void> {

    @Override
    public Void visit(AST ast) {
        switch(ast.token().type()) {
            case SEQUENCE:
                printSequence(ast); break;
            case LEFT_BRACKET:
            case LEFT_BRACKET_CARET:
                printCharacterClass(ast);
                break;
            case ZERO_OR_ONE:
            case ZERO_OR_MORE:
            case ONE_OR_MORE:
                printQuantifier(ast);
                break;
            case CHARACTER:
                printCharacter(ast);
                break;
            case INTERVAL:
                printInterval(ast);
                break;
            default:
                throw new Error("Unexpected node type " + ast);
        }
        return null;
    }

    private void printInterval(AST interval) {
        interval.children().get(0).visit(this);
        System.out.print("-");
        interval.children().get(1).visit(this);
    }

    private void printSequence(AST sequence) {
        for (AST child : sequence.children()) {
            child.visit(this);
        }
    }

    private void printCharacterClass(AST charClass) {
        System.out.print(charClass.token().text());
        for (AST child : charClass.children()) {
            child.visit(this);
        }
        System.out.print("]");
    }

    private void printQuantifier(AST quantifier) {
        quantifier.children().get(0).visit(this);
        System.out.print(quantifier.token().text());
    }

    private void printCharacter(AST character) {
        System.out.print(character.token().text());
    }
}
