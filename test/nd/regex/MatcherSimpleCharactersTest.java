package nd.regex;

/**
 *
 */
public class MatcherSimpleCharactersTest extends MatcherTestCase {

    public void test_characters() {
        checkWorkEqually("abc", "abc");
        checkWorkEqually("abc", "b");
    }

    public void test_escaped_characters() {
        checkWorkEqually("a\\b",   "a\\\\b");
        checkWorkEqually("\\d",    "a\\\\d");
        checkWorkEqually("(){}[]", "\\(\\)\\{\\}\\[\\]");
        checkWorkEqually("[a]",    "\\[a\\]");
        checkWorkEqually("[a]",    "[a]");
        checkWorkEqually("-",      "\\-");        
    }

}
