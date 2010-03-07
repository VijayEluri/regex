package nd.regex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
class NFA {

    abstract static class State {
        static private int seq = 0;
        private boolean isFinal;
        protected final int id;

        State() {
            id = seq++;
        }

        abstract List<State> step(Character c);
        abstract boolean match(Character c);
        abstract void patch(State s);

        boolean isFinal() {return isFinal;}
        void setFinal(boolean aFinal) {isFinal = aFinal;}

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
        private final Character c;
        private State output;

        CharacterState(Character c) {
            this.c = c;
        }

        @Override
        List<State> step(Character c) {
            if (output != null) {
                return match(c) ? Collections.singletonList(output) : Collections.<State>emptyList();
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        boolean match(Character c) {
            return this.c.equals(c);
        }

        @Override
        void patch(State s) {
            if (output == null) {
                output = s;
            } else {
                output.patch(s);
            }
        }

        @Override
        public String toString() {
            return "(s" + id +")-" + c.toString() + "->" + (output != null ? output.toString() : "");
        }
    }

    static class CharacterIntervalState extends State {
        private final Character c;
        private State output;

        CharacterIntervalState(Character c) {
            this.c = c;
        }

        @Override
        List<State> step(Character c) {            
            if (output != null) {
                return match(c) ? Collections.singletonList(output) : Collections.<State>emptyList();
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        boolean match(Character c) {
            return this.c.equals(c);
        }

        @Override
        void patch(State s) {
            if (output == null) {
                output = s;
            } else {
                output.patch(s);
            }
        }

        @Override
        public String toString() {
            return "(s" + id +")-" + c.toString() + "->" + (output != null ? output.toString() : "");
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
        boolean match(Character c) {
            return s1.match(c) || s2.match(c);
        }

        @Override
        void patch(State s) {
            s1.patch(s);
            s2.patch(s);
        }

        @Override
        public String toString() {
            return String.format("(s%d)-(OR %s %s)", id, s1, s2);
        }

        @Override
        boolean isFinal() {
            return s1.isFinal() || s2.isFinal();
        }

        @Override
        void setFinal(boolean aFinal) {
            s1.setFinal(aFinal);
            s2.setFinal(aFinal);
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
        boolean match(Character c) {
            return s1.match(c);
        }

        @Override
        void patch(State s) {
            s2.patch(s);
        }

        @Override
        public String toString() {
            return String.format("(s%d)-(AND %s %s)", id, s1, s2);
        }

        @Override
        boolean isFinal() {
            return s1.isFinal();
        }

        @Override
        void setFinal(boolean aFinal) {
            s1.setFinal(aFinal);
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
        void patch(State s) {
            output = s;
        }

        @Override
        boolean match(Character c) {
            return true;
        }

        @Override
        public String toString() {
            return String.format("(s%d)-any-> %s)", id, output);
        }

        @Override
        void setFinal(boolean aFinal) {
            super.setFinal(aFinal);
        }

        @Override
        boolean isFinal() {
            boolean finalOutput = (output != null) ? output.isFinal() : false;
            return super.isFinal() || finalOutput;
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
        void patch(State s) {
            //ignore
        }
        @Override
        boolean match(Character c) {
            return s.match(c);
        }
        @Override
        public String toString() {
            return s.toString();
        }
        @Override
        boolean isFinal() {
            return s.isFinal();
        }
        @Override
        void setFinal(boolean aFinal) {
            s.setFinal(aFinal);
        }
    }
}
