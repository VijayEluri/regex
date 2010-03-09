package nd.regex;

/**
 *
 */
public class GroupTest extends MatcherTestCase {

    public void test_group() {
        checkWorkEqually("ab", "(ab)");
        checkWorkEqually("ababab", "(ab)+");
        checkWorkEqually("", "(ab)*");
    }


    public void test_alternative() {
        checkWorkEqually("a", "a|b");
        checkWorkEqually("am", "am|pm");
        checkWorkEqually("pm", "am|pm");
        checkWorkEqually("ss", "am|pm|ss");
        checkWorkEqually("aba", "(a|b){3}");
        checkWorkEqually("ababa", "(a|b){3,5}");
    }

}
