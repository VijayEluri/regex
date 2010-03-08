package nd.regex;

import junit.framework.TestCase;

import java.util.*;
import java.util.regex.Pattern;

/**
 *
 */
public class NFATest2 {


    public void _test() {
        String pattern = "a?a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?.a?."+
                "a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.";

        Stack<State> stack = new Stack<State>();

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            boolean sinc = i == pattern.length() - 1;
            switch (c) {
                case '.' : {
                    State s2 = stack.pop();
                    State s1 = stack.pop();
                    State and = new AndState(s1, s2);
                    if (sinc) {
                        State f = new EmptyState();f.setFinal(true);
                        s2.patch(f);
                    }
                    stack.push(and);
                    break;
                }
                case '|' : {
                    State s2 = stack.pop();
                    State s1 = stack.pop();
                    stack.push(new OrState(s1, s2));
                    break;
                }
                case '?' : {
                    State s = stack.pop();
                    stack.push(new OrState(s, new EmptyState()));
                    break;
                }
                case '*' : {
                    State s = stack.pop();
                    s.patch(new Unpatchable(s));
                    stack.push(new OrState(s, new EmptyState()));
                    break;
                }
                case '+' : {
                    State s = stack.pop();
                    State or = new OrState(new Unpatchable(s), new EmptyState());
                    s.patch(or);
                    stack.push(s);
                    break;
                }
                default : {
                    State s = new CharacterState(c);
                    stack.push(s);
                    break;
                }
            }
        }

        State s = stack.pop();

        String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        boolean found = false;
        Set<State> current = new HashSet<State>();
        current.add(s);
        for (int i = 0; i < str.length(); i++) {
            Character c = str.charAt(i);
            Set<State> next = new HashSet<State>();
            for (State state : current) {
                if (state.isFinal()) found = true;
                break;
            }
            for (State state : current) {
                if (state != null && state.match(c))
                    next.addAll(state.step(c));
            }
            current = next;
            current.add(s);
        }
        for (State state : current) {
            if (state.isFinal()) found = true;
        }
        System.out.println(found);


        Pattern p = Pattern.compile(pattern.replaceAll("\\.", ""));
        System.out.println(p.matcher(str).matches());
    }

    static interface Matchable {
        boolean match(Character c);
    }

    abstract static class State implements Matchable {

        private boolean isFinal;
        private static int seq = 0;
        private final int id;

        State() {
            id = seq++;
        }

        abstract List<State> step(Character c);

        public boolean isFinal() {
            return isFinal;
        }

        public void setFinal(boolean aFinal) {
            isFinal = aFinal;
        }

        abstract public void patch(State s);

        @Override
        public final boolean equals(Object other) {
            if (!(other instanceof State)) return false;
            return id == ((State) other).id;
        }

        @Override
        public final int hashCode() {
            return id;
        }
    }


    static class CharacterState extends State {
        private Character c;
        private State output;

        CharacterState(Character c) {
            this.c = c;
        }

        @Override
        List<State> step(Character c) {
            if (output != null)
                return match(c) ? Collections.singletonList(output) : Collections.<State>emptyList();
            else
                return Collections.emptyList();
        }

        @Override
        public boolean match(Character c) {
            return this.c.equals(c);
        }

        @Override
        public void patch(State s) {
            if (output == null) {
                output = s;
            } else {
                output.patch(s);
            }
        }

        @Override
        public String toString() {
            return c.toString() + "->" + (output != null ? output.toString() : "null");
        }
    }


    static class OrState extends State {

        private State s1;
        private State s2;

        OrState(State s1, State s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        @Override
        List<State> step(Character c) {
            List<State> result = new ArrayList<State>();
            result.addAll(s1.step(c));
            result.addAll(s2.step(c));
            return result;
        }

        @Override
        public boolean match(Character c) {
            return s1.match(c) || s2.match(c);
        }

        @Override
        public void patch(State s) {
            s1.patch(s);
            s2.patch(s);
        }

        @Override
        public String toString() {
            return "(OR " + s1 + " " + s2 + ")";
        }
    }

    static class AndState extends State {

        private State s1;
        private State s2;

        AndState(State s1, State s2) {
            this.s1 = s1;
            this.s2 = s2;
            s1.patch(s2);
        }

        @Override
        List<State> step(Character c) {
            return s1.step(c);
        }

        @Override
        public boolean match(Character c) {
            return s1.match(c);
        }

        @Override
        public void patch(State s) {
            s2.patch(s);
        }

        @Override
        public String toString() {
            return "(AND " + s1 + " " + s2 + ")";
        }
    }

    static class EmptyState extends State {

        private State output;

        @Override
        List<State> step(Character c) {
            if (output != null) {
                return output.step(c);
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        public void patch(State s) {
            output = s;
        }

        @Override
        public boolean match(Character c) {
            return true;
        }

        @Override
        public String toString() {
            return "e->" + output;
        }
    }

    static class Unpatchable extends State {
        private State s;
        Unpatchable(State s) {
            this.s = s;
        }
        @Override
        List<State> step(Character c) {
            return s.step(c);
        }

        @Override
        public void patch(State s) {
            //ignore
        }

        @Override
        public boolean match(Character c) {
            return s.match(c);
        }

        @Override
        public String toString() {
            return s.toString();
        }
    }
}
