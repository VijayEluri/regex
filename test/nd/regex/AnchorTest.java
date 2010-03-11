package nd.regex;

/**
 *
 */
public class AnchorTest extends MatcherTestCase {

    public void test_single_line() {
        checkWorkEqually("abc", "^abc");
        checkWorkEqually("a\nbc", ".*^bc");
        checkWorkEqually("abc", "a^bc");
        checkWorkEqually("abc", "abc$");
        checkWorkEqually("abc", "ab$c");
        checkWorkEqually("", "^$");
        checkWorkEqually("", "$");
        checkWorkEqually("", "^");
        checkWorkEqually("abc", "^(.*)$");
    }

}
