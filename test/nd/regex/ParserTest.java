package nd.regex;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

import static nd.regex.Token.Type;
import static nd.regex.NFA.*;

/**
 *
 */
public class ParserTest extends TestCase {

    public void test_usual_characters() {
        Parser parser = new ParserImpl(new LexerImpl("abc"));
        AST ast = parser.parse();
        AST a = ast.children().get(0);
        AST b = ast.children().get(1);
        AST c = ast.children().get(2);
        assertEquals(new Token(Type.CHARACTER, "a"), a.token());
        assertTrue(a.children().isEmpty());
        assertEquals(new Token(Type.CHARACTER, "b"), b.token());
        assertTrue(b.children().isEmpty());
        assertEquals(new Token(Type.CHARACTER, "c"), c.token());
        assertTrue(c.children().isEmpty());
    }

    public void test_quantifier() {
        Parser parser = new ParserImpl(new LexerImpl("a?b+c*"));
        AST ast = parser.parse();
        assertEquals(3, ast.children().size());
        AST q1 = ast.children().get(0);
        AST q2 = ast.children().get(1);
        AST q3 = ast.children().get(2);
        assertEquals(new Token(Type.ZERO_OR_ONE, "?"), q1.token());
        assertEquals(1, q1.children().size());
        assertEquals(new Token(Type.CHARACTER, "a"), q1.children().get(0).token());
        assertEquals(new Token(Type.ONE_OR_MORE, "+"), q2.token());
        assertEquals(1, q2.children().size());
        assertEquals(new Token(Type.CHARACTER, "b"), q2.children().get(0).token());
        assertEquals(new Token(Type.ZERO_OR_MORE, "*"), q3.token());
        assertEquals(1, q3.children().size());
        assertEquals(new Token(Type.CHARACTER, "c"), q3.children().get(0).token());
    }

    public void test_character_class_simple() {
        Parser parser = new ParserImpl(new LexerImpl("a[abc]b"));
        AST ast = parser.parse();
        assertEquals(3, ast.children().size());
        AST charClass = ast.children().get(1);
        assertEquals(new Token(Type.LEFT_BRACKET, "["), charClass.token());
        assertEquals(3, charClass.children().size());
    }

    public void test_character_class_interval() {
        Parser parser = new ParserImpl(new LexerImpl("[ab-eo-z]"));
        AST ast = parser.parse();
        AST charClass = ast.children().get(0);
        assertEquals(3, charClass.children().size());
        assertEquals(new Token(Type.INTERVAL, "-"), charClass.children().get(1).token());
        assertEquals(new Token(Type.INTERVAL, "-"), charClass.children().get(2).token());
        AST interval1 = charClass.children().get(1);
        AST interval2 = charClass.children().get(2);
        assertEquals(2, interval1.children().size());
        assertEquals(2, interval2.children().size());
        assertEquals(new Token(Type.CHARACTER, "b"), interval1.children().get(0).token());
        assertEquals(new Token(Type.CHARACTER, "e"), interval1.children().get(1).token());
        assertEquals(new Token(Type.CHARACTER, "o"), interval2.children().get(0).token());
        assertEquals(new Token(Type.CHARACTER, "z"), interval2.children().get(1).token());
    }

    public void test_print() {
        Parser parser = new ParserImpl(new LexerImpl("abc[a-z]*o?"));
        AST ast = parser.parse();
        ast.visit(new PrintVisitor());
    }

    public void test_nfa() {
        Parser parser = new ParserImpl(new LexerImpl("abc*"));
        AST ast = parser.parse();
        NFA.State s = ast.visit(new NFABuilder());

        String str = "abccccc";
        boolean found = false;
        Set<State> current = new HashSet<State>();
        current.add(s);
        for (int i = 0; i < str.length(); i++) {
            Character c = str.charAt(i);
            Set<State> next = new HashSet<State>();
            for (State state : current) {
                if (state.isFinal()) {
                    found = true;
                    break;
                }
            }
            for (State state : current) {
                if (state != null && state.match(c))
                    next.addAll(state.step(c));
            }
            current = next;
            current.add(s);
        }
        if (!found) {
            for (State state : current) {
                if (state.isFinal()) found = true;
            }
        }
        System.out.println(found);
    }

}
