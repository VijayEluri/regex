package nd.regex;

import java.util.regex.Pattern;

/**
 *
 */
public class MiscTest extends MatcherTestCase {

    public void test_pathological_pattern() {
        for (int i = 1; i < 25; i++) {
            String pattern = createPathologicalPattern('a', i);
            String str = createStringToMatch('a', i);

            long start = System.currentTimeMillis();
            Matcher.matches(str, pattern);
            long result1 = System.currentTimeMillis() - start;

            start = System.currentTimeMillis();
            Pattern.compile(pattern).matcher(str).matches();
            long result2 = System.currentTimeMillis() - start;

            System.out.println(i + ": " + result1 + " " +  result2);
        }
    }


    private String createPathologicalPattern(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(String.valueOf(c)).append("?");
        }
        for (int i = 0; i < n; i++) {
            sb.append(String.valueOf(c));
        }
        return sb.toString();
    }

    private String createStringToMatch(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(String.valueOf(c));
        }
        return sb.toString();
    }

}
