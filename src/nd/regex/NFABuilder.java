package nd.regex;

import java.util.Iterator;

import static nd.regex.NFA.*;

/**
 *
 */
class NFABuilder implements ASTNodeVisitor<State> {

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
            if (!iter.hasNext()) {
                State f = new EmptyState();
                f.setFinal(true);
                childState.patch(f);
            }
        }
        return first;
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
        if (first == null) {
            first = quantifier.term().visit(this);
            first.patch(new Unpatchable(first));
            first = new OrState(first, new EmptyState());
        } else {
            State or = new OrState(new Unpatchable(first), new EmptyState());
            first.patch(or);
        }
        return first;
    }

    @Override
    public State visit(CharacterClassNode charClass) {
        State charClassState = null;
        for (AST child : charClass.children()) {
            if (charClassState == null) {
                charClassState = child.visit(this);
            } else {
                charClassState = new OrState(charClassState, child.visit(this));
            }
        }
        if (charClassState == null) {
            charClassState = new EmptyState();
        }
        return charClassState;
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
}
