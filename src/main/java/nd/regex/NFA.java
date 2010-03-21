package nd.regex;

import java.util.*;

/**
 * Non-deterministic Finite Automaton (NFA).
 * NFA is just set of states properly linked with each other.
 */
class NFA {

    /**
     * Emulate work of NFA from its first state
     * @param str string to work with
     * @param firstState first state of NFA to emulate
     * @return true if NFA match string, otherwise false
     */
    static boolean emulate(String str, State firstState) {
        Set<State> current = new HashSet<State>();
        current.add(firstState);
        for (int i = 0; i < str.length(); i++) {
            char prevChar = i == 0 ? 0 : str.charAt(i - 1);
            char nextChar = i + 1 >= str.length() ? 0 : str.charAt(i + 1);
            Character c = str.charAt(i);
            Set<State> next = new HashSet<State>();
            for (State state : current) {
                next.addAll(state.step(prevChar, c, nextChar));
            }
            current = next;
        }
        for (State state : current) {
            if (state.isFinal()) return true;
        }
        return false;
    }

    /**
     * Base State class, defines interface for other states.
     */
    abstract static class State {

        static private int seq = 0;
        private boolean isFinal;
        protected final int id;

        State() {
            id = seq++;
        }

        /**
         * Step through state
         * @param prev previous character
         * @param cur current character
         * @param next next character
         * @return list of next states to match
         */
        abstract List<State> step(Character prev, Character cur, Character next);

        /**
         * Check if this state matches previous, current and next characters
         * @param prev previous character
         * @param cur current character
         * @param next next character
         * @return true if matches, false otherwise
         */
        abstract boolean match(Character prev, Character cur, Character next);

        /**
         * Link state with next state
         * @param s next state
         */
        abstract void patch(State s);

        /**
         * Check if this state is final
         * @return true if state is final
         */
        boolean isFinal() {
            return isFinal;
        }

        /**
         * Make this state final flag
         * @param aFinal final flag
         */
        void setFinal(boolean aFinal) {
            isFinal = aFinal;
        }

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


    /**
     * State for single character
     */
    static class CharacterState extends State {
        private final Character c;
        private State output;

        CharacterState(Character c) {
            this.c = c;
        }

        @Override
        List<State> step(Character prev, Character cur, Character next) {
            if (output != null) {
                return match(prev, cur, next) ? Collections.singletonList(output) : Collections.<State>emptyList();
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        boolean match(Character prev, Character cur, Character next) {
            return this.c.equals(cur);
        }

        @Override
        void patch(State s) {
            if (output == s) return;
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

    /**
     * State for any character (.)
     */
    static class AnyCharacterState extends State {
        private State output;

        AnyCharacterState() {
        }

        @Override
        List<State> step(Character prev, Character cur, Character next) {
            if (output != null) {
                return Collections.singletonList(output);
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        boolean match(Character prev, Character cur, Character next) {
            return true;
        }

        @Override
        void patch(State s) {
            if (output == s) return;
            if (output == null) {
                output = s;
            } else {
                output.patch(s);
            }
        }

        @Override
        public String toString() {
            return "(s" + id +")-any->" + (output != null ? output.toString() : "");
        }
    }

    /**
     * State for interval in character class
     */
    static class CharacterIntervalState extends State {
        private final char lowBound;
        private final char highBound;
        private State output;

        CharacterIntervalState(char lowBound, char highBound) {
            this.lowBound = lowBound;
            this.highBound = highBound;
        }

        @Override
        List<State> step(Character prev, Character cur, Character next) {
            if (output != null) {
                return match(prev, cur, next) ? Collections.singletonList(output) : Collections.<State>emptyList();
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        boolean match(Character prev, Character cur, Character next) {
            return cur >= lowBound && cur <= highBound;
        }

        @Override
        void patch(State s) {
            if (output == s) return;
            if (output == null) {
                output = s;
            } else {
                output.patch(s);
            }
        }

        @Override
        public String toString() {
            return "(s" + id +")-" + "(" + Character.toString(lowBound) + "-" + Character.toString(highBound) + ")" + "->"
                    + (output != null ? output.toString() : "");
        }
    }

    /**
     * Used to match one of two alternatives
     */
    static class OrState extends State {
        private final State s1;
        private final State s2;

        OrState(State s1, State s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        @Override
        List<State> step(Character prev, Character cur, Character next) {
            List<State> result = new ArrayList<State>();
            result.addAll(s1.step(prev, cur, next));
            result.addAll(s2.step(prev, cur, next));
            return result;
        }

        @Override
        boolean match(Character prev, Character cur, Character next) {
            return s1.match(prev, cur, next) || s2.match(prev, cur, next);
        }

        @Override
        void patch(State s) {
            s1.patch(s);
            s2.patch(s);
        }

        @Override
        public String toString() {
            return String.format("(OR%s %s %s)", id, s1, s2);
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

    /**
     * Wrapper over other state, matches if other state doesn't match and vise versa
     */
    static class NotState extends State {
        private final State s;
        private State output;

        NotState(State s) {
            this.s = s;
        }

        @Override
        List<State> step(Character prev, Character cur, Character next) {
            if (output != null && match(prev, cur, next)) {
                return Collections.singletonList(output);
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        boolean match(Character prev, Character cur, Character next) {
            return !s.match(prev, cur, next);
        }

        @Override
        void patch(State s) {
            if (output == s) return;
            if (output == null) {
                output = s;
            } else {
                output.patch(s);
            }
        }

        @Override
        public String toString() {
            return String.format("(NOT%d %s %s)", id, s, output != null ? output : "");
        }
    }

    /**
     * State that always matches
     */
    static class EmptyState extends State {
        protected State output;

        @Override
        List<State> step(Character prev, Character cur, Character next) {
            if (output != null && match(prev, cur, next)) {
                return output.step(prev, cur, next);
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        void patch(State s) {
            if (output == s) return;
            if (output == null) {
                output = s;
            } else {
                output.patch(s);
            }
        }

        @Override
        boolean match(Character prev, Character cur, Character next) {
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

    /**
     * State for start of the line. Matches only if previous character is 0
     */
    static class LineStartState extends EmptyState {
        @Override
        boolean match(Character prev, Character cur, Character next) {
            return (prev == 0) && output.match(prev, cur, next);
        }

        @Override
        public String toString() {
            return String.format("(s%d)^-> %s)", id, output);
        }
    }

    /**
     * State for line end. Never matches, so any pattern with $ in the middle always fails.
     */
    static class LineEndState extends EmptyState {
        @Override
        boolean match(Character prev, Character cur, Character next) {
            return false;
        }

        @Override
        public String toString() {
            return String.format("(s%d)->$ %s)", id, output);
        }
    }

    /**
     * Wrapper over other state, protects wrapped state from patching with itself
     */
    static class Unpatchable extends State {
        private State s;
        Unpatchable(State s) {
            this.s = s;
        }
        @Override
        List<State> step(Character prev, Character cur, Character next) {
            return s.step(prev, cur, next);
        }
        @Override
        void patch(State s) {
            //ignore
        }
        @Override
        boolean match(Character prev, Character cur, Character next) {
            return s.match(prev, cur, next);
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
