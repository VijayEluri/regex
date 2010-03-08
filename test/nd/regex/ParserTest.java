package nd.regex;

import junit.framework.TestCase;

import static nd.regex.Token.Type;

/**
 *
 */
public class ParserTest extends TestCase {

    public void test_usual_characters() {
        Parser parser = new ParserImpl(new LexerImpl("abc"));
        AST ast = parser.parse();
        CharacterNode a = (CharacterNode) ast.children().get(0);
        CharacterNode b = (CharacterNode) ast.children().get(1);
        CharacterNode c = (CharacterNode) ast.children().get(2);
        assertEquals(new Token(Type.CHARACTER, "a"), a.token());
        assertTrue(a.children().isEmpty());
        assertEquals(new Token(Type.CHARACTER, "b"), b.token());
        assertTrue(b.children().isEmpty());
        assertEquals(new Token(Type.CHARACTER, "c"), c.token());
        assertTrue(c.children().isEmpty());
    }

    public void test_quantifier() {
        Parser parser = new ParserImpl(new LexerImpl("a?b+c*d{1,3}e{7,}z{5}"));
        AST ast = parser.parse();
        assertEquals(6, ast.children().size());
        AST q1 = ast.children().get(0);
        AST q2 = ast.children().get(1);
        AST q3 = ast.children().get(2);
        BoundedQuantifierNode q4 = (BoundedQuantifierNode) ast.children().get(3);
        UnboundedQuantifierNode q5 = (UnboundedQuantifierNode) ast.children().get(4);
        BoundedQuantifierNode q6 = (BoundedQuantifierNode) ast.children().get(5);
        assertEquals(new Token(Type.ZERO_OR_ONE, "?"), q1.token());
        assertEquals(1, q1.children().size());
        assertEquals(new Token(Type.CHARACTER, "a"), q1.children().get(0).token());

        assertEquals(new Token(Type.ONE_OR_MORE, "+"), q2.token());
        assertEquals(1, q2.children().size());
        assertEquals(new Token(Type.CHARACTER, "b"), q2.children().get(0).token());

        assertEquals(new Token(Type.ZERO_OR_MORE, "*"), q3.token());
        assertEquals(1, q3.children().size());
        assertEquals(new Token(Type.CHARACTER, "c"), q3.children().get(0).token());

        assertEquals(1, q4.lowBound());
        assertEquals(3, q4.highBound());
        assertEquals(new Token(Type.CHARACTER, "d"), q4.term().token());

        assertEquals(7, q5.lowBound());
        assertEquals(new Token(Type.CHARACTER, "e"), q5.term().token());

        assertEquals(5, q6.lowBound());
        assertEquals(5, q6.lowBound());
        assertEquals(new Token(Type.CHARACTER, "z"), q6.term().token());
    }

    public void test_character_class_simple() {
        Parser parser = new ParserImpl(new LexerImpl("a[]abc]b"));
        AST ast = parser.parse();
        assertEquals(3, ast.children().size());
        CharacterClassNode charClass = (CharacterClassNode) ast.children().get(1);
        assertEquals(new Token(Type.LEFT_BRACKET, "["), charClass.token());
        assertEquals(4, charClass.children().size());
        assertEquals(new Token(Type.CHARACTER, "]"), charClass.children().get(0).token());
    }

    public void test_character_class_inner() {
        Parser parser = new ParserImpl(new LexerImpl("a[]a[^bc]]b"));
        AST ast = parser.parse();
        assertEquals(3, ast.children().size());

        CharacterClassNode charClass = (CharacterClassNode) ast.children().get(1);
        assertEquals(new Token(Type.LEFT_BRACKET, "["), charClass.token());
        assertEquals(3, charClass.children().size());
        assertEquals(new Token(Type.CHARACTER, "]"), charClass.children().get(0).token());
        assertEquals(new Token(Type.CHARACTER, "a"), charClass.children().get(1).token());

        CharacterClassNode inner = (CharacterClassNode) charClass.children().get(2);
        assertEquals(new Token(Type.LEFT_BRACKET_CARET, "[^"), inner.token());
        assertEquals(2, inner.children().size());
        assertEquals(new Token(Type.CHARACTER, "b"), inner.children().get(0).token());
        assertEquals(new Token(Type.CHARACTER, "c"), inner.children().get(1).token());
    }

    public void test_character_class_interval() {
        Parser parser = new ParserImpl(new LexerImpl("[ab-eo-z]"));
        AST ast = parser.parse();
        CharacterClassNode charClass = (CharacterClassNode) ast.children().get(0);
        assertEquals(3, charClass.children().size());
        assertEquals(new Token(Type.INTERVAL, "-"), charClass.children().get(1).token());
        assertEquals(new Token(Type.INTERVAL, "-"), charClass.children().get(2).token());
        CharacterClassIntervalNode interval1 = (CharacterClassIntervalNode) charClass.children().get(1);
        CharacterClassIntervalNode interval2 = (CharacterClassIntervalNode) charClass.children().get(2);
        assertEquals(2, interval1.children().size());
        assertEquals(2, interval2.children().size());
        assertEquals(new Token(Type.CHARACTER, "b"), interval1.lowBound().token());
        assertEquals(new Token(Type.CHARACTER, "e"), interval1.highBound().token());
        assertEquals(new Token(Type.CHARACTER, "o"), interval2.lowBound().token());
        assertEquals(new Token(Type.CHARACTER, "z"), interval2.highBound().token());
    }

}
