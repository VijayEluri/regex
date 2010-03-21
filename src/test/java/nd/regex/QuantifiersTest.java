package nd.regex;

/**
 *
 */
public class QuantifiersTest extends MatcherTestCase {


    public void test_predefined_quantifiers() {
        checkWorkEqually("a",   "a?");
        checkWorkEqually("",    "a?");
        checkWorkEqually("a",   "a*");
        checkWorkEqually("",    "a*");
        checkWorkEqually("aaa", "a*");
        checkWorkEqually("a",   "a+");
        checkWorkEqually("",    "a+");
        checkWorkEqually("aaa", "a+");
    }


    public void test_user_defined_quantifiers() {
        checkWorkEqually("aaa", "a{1,}");
        checkWorkEqually("aaa", "a{1,3}");
        checkWorkEqually("aaa", "a{1,2}");
        checkWorkEqually("aa",  "a{1,0}");
        checkWorkEqually("aa",  "a{1,-1}");
        checkWorkEqually("aa",  "a{-1,2}");
        checkWorkEqually("aa",  "a{3}");
        checkWorkEqually("aaa", "a{3}");
        checkWorkEqually("555-22-33", "\\d{3}-\\d{2}-\\d{2}");
    }

}
