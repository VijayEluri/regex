package nd.regex;

import java.util.Iterator;

import static nd.regex.NFA.*;

/**
 * NFA builder.
 * Build NFA from AST.
 */
final class NFABuilder implements ASTVisitor<State> {

    @Override
    public State visit(SequenceNode sequence) {
        State first = null;
        for (Iterator<AST> iter = sequence.children().iterator(); iter.hasNext();) {
            State childState = iter.next().visit(this);
            if (first != null) {
                first.patch(childState);
            } else {
                first = childState;
            }
            if (!iter.hasNext() && sequence.isRoot()) {
                State f = new EmptyState();
                f.setFinal(true);
                childState.patch(f);
            }
        }
        //empty pattern always match
        if (first == null) {
            first = new EmptyState();
            first.setFinal(true);
        }
        return first;
    }

    @Override
    public State visit(AlternativeNode alternative) {
        State first = null;
        for (AST child : alternative.getFirstAlternative()) {
            State childState = child.visit(this);
            if (first != null) {
                first.patch(childState);
            } else {
                first = childState;
            }
        }
        if (first == null) first = new EmptyState();
        State second = null;
        for (AST child : alternative.getSecondAlternative()) {
            State childState = child.visit(this);
            if (second != null) {
                second.patch(childState);
            } else {
                second = childState;
            }
        }
        if (second == null) second = new EmptyState();
        return new OrState(first, second);
    }

    @Override
    public State visit(BoundedQuantifierNode quantifier) {
        State first = null;
        for (int i = 0; i < quantifier.lowBound(); i++) {
            State s = quantifier.term().visit(this);
            if (first == null) {
                first = s;
            } else {
                first.patch(s);
            }
        }
        for (int i = quantifier.lowBound(); i < quantifier.highBound(); i++) {
            if (first == null) {
                first = new OrState(quantifier.term().visit(this), new EmptyState());
            } else {
                first.patch(new OrState(quantifier.term().visit(this), new EmptyState()));
            }
        }
        return first;
    }

    @Override
    public State visit(UnboundedQuantifierNode quantifier) {
        State first = null;
        for (int i = 0; i < quantifier.lowBound(); i++) {
            State s = quantifier.term().visit(this);
            if (first == null) {
                first = s;
            } else {
                first.patch(s);
            }
        }

        State last = quantifier.term().visit(this);
        State or = new OrState(last, new EmptyState());
        last.patch(new Unpatchable(or));
        if (first == null) {
            first = or;
        } else {
            first.patch(or);
        }
        return first;
    }

    @Override
    public State visit(CharacterClassNode charClass) {
        State charClassState = null;
        if (charClass.token().type() == Token.Type.CLASS_ANY_CHARACTER) {
            charClassState = new AnyCharacterState();
        } else {
            for (AST child : charClass.children()) {
                if (charClassState == null) {
                    charClassState = child.visit(this);
                } else {
                    charClassState = new OrState(charClassState, child.visit(this));
                }
            }
        }
        if (charClassState == null) {
            return new EmptyState();
        } else if (charClass.exclusive()) {
            return new NotState(charClassState);
        } else {
            return charClassState;
        }
    }

    @Override
    public State visit(CharacterClassIntervalNode interval) {
        return new CharacterIntervalState(interval.lowBound().token().text().charAt(0),
                interval.highBound().token().text().charAt(0));
    }

    @Override
    public State visit(CharacterNode character) {
        return new CharacterState(character.token().text().charAt(0));
    }

    @Override
    public State visit(AnchorNode anchor) {
        switch (anchor.token().type()) {
            case CARET:  return new LineStartState();
            case DOLLAR: return new LineEndState();
            default: throw new Error("Unexpected token");
        }
    }
}
