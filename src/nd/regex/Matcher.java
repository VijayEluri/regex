package nd.regex;

import java.util.HashSet;
import java.util.Set;

import static nd.regex.NFA.State;

/**
 *
 */
public class Matcher {

    public static boolean matches(String str, String pattern) {
        Parser parser = new ParserImpl(new LexerImpl(pattern));
        AST ast = parser.parse();
        State first = ast.visit(new NFABuilder());
        Set<State> current = new HashSet<State>();
        current.add(first);
        for (int i = 0; i < str.length(); i++) {
            Character c = str.charAt(i);
            Set<State> next = new HashSet<State>();
            for (State state : current) {
                next.addAll(state.step(c));
            }
            current = next;
        }
        for (State state : current) {
            if (state.isFinal()) return true;
        }
        return false;
    }

}
