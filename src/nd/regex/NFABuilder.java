package nd.regex;

import java.util.Iterator;

import static nd.regex.NFA.*;

/**
 *
 */
class NFABuilder implements ASTVisitor<State> {

    private State result = null;

    @Override
    public State visit(AST ast) {
        switch(ast.token().type()) {
            case SEQUENCE:
                return buildSequence(ast);
            case LEFT_BRACKET:
            case LEFT_BRACKET_CARET:
                buildCharacterClass(ast);
                break;
            case ZERO_OR_ONE:
            case ZERO_OR_MORE:
            case ONE_OR_MORE:
                return buildQuantifier(ast);
            case CHARACTER:
                return buildCharacter(ast);
            case INTERVAL:
                buildInterval(ast);
                break;
            default:
                throw new Error("Unexpected node type " + ast);
        }
        return result;
    }

    private void buildInterval(AST interval) {
        State s = interval.children().get(0).visit(this);
        System.out.print("-");
        interval.children().get(1).visit(this);
    }

    private State buildSequence(AST sequence) {
        State result = null;
        for (Iterator<AST> iter = sequence.children().iterator(); iter.hasNext();) {
            State childState = iter.next().visit(this);
            if (result != null) {
                result = new AndState(result, childState);;
            } else {
                result = childState;
            }
            if (!iter.hasNext()) {
                State f = new EmptyState();f.setFinal(true);
                childState.patch(f);
            }
        }
        return result;
    }

    private void buildCharacterClass(AST charClass) {
        System.out.print(charClass.token().text());
        for (AST child : charClass.children()) {
            child.visit(this);
        }
        System.out.print("]");
    }

    private State buildQuantifier(AST quantifier) {
        if (quantifier.token().text().equals("?")) {
            return new OrState(quantifier.children().get(0).visit(this), new EmptyState());
        } else if (quantifier.token().text().equals("+")) {
            State child = quantifier.children().get(0).visit(this);
            State or = new OrState(new Unpatchable(child), new EmptyState());
            child.patch(or);
            return child;
        } else if (quantifier.token().text().equals("*")) {
            State child = quantifier.children().get(0).visit(this);
            child.patch(new Unpatchable(child));
            return new OrState(child, new EmptyState());
        } else {
            throw new Error("Unknown quantifier");
        }
    }

    private State buildCharacter(AST character) {
        return new CharacterState(character.token().text().charAt(0));
    }

}
