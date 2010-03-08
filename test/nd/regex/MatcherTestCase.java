package nd.regex;

import junit.framework.TestCase;

import java.util.regex.Pattern;

/**
 *
 */
abstract public class MatcherTestCase extends TestCase {

    protected void checkWorkEqually(String str, String pattern) {
        boolean buildInResult = false;
        boolean buildIndExceptionsThrown = false;
        boolean result = false;
        boolean exceptionThrown = false;
        try {
            buildInResult = Pattern.compile(pattern).matcher(str).matches();
        } catch (Throwable t) {
            buildIndExceptionsThrown = true;
        }

        try {
            result = Matcher.matches(str, pattern);
        } catch (Throwable t) {
            exceptionThrown = true;
        }

        if (buildIndExceptionsThrown) {
            assertTrue("Exception should be thrown", exceptionThrown);
        } else {
            assertEquals("Wrong result", buildInResult, result);
        }
    }

}
