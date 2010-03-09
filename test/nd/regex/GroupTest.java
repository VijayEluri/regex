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

}
