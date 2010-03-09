package nd.regex;

import static nd.regex.NFA.State;

/**
 * Single entry point to regexp engine.
 *
 * <a name="sum">
 * <h4> Compatibility with {@link java.util.regex.Pattern} </h4>
 *
 * <table border="0" cellpadding="1" cellspacing="0"
 *  summary="Regular expression constructs, and what they match">
 *
 * <tr align="left">
 * <th bgcolor="#CCCCFF" align="left" id="construct">Construct</th>
 * <th bgcolor="#CCCCFF" align="left" id="matches">Matches</th>
 * <th bgcolor="#CCCCFF" align="left" id="matches">Supported</th>
 * </tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="characters">Characters</th></tr>
 *
 * <tr><td valign="top" headers="construct characters"><i>x</i></td>
 *     <td headers="matches">The character <i>x</i></td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\\</tt></td>
 *     <td headers="matches">The backslash character</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\0</tt><i>n</i></td>
 *     <td headers="matches">The character with octal value <tt>0</tt><i>n</i>
 *         (0&nbsp;<tt>&lt;=</tt>&nbsp;<i>n</i>&nbsp;<tt>&lt;=</tt>&nbsp;7)</td>
 *     <td headers="matches">-</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\0</tt><i>nn</i></td>
 *     <td headers="matches">The character with octal value <tt>0</tt><i>nn</i>
 *         (0&nbsp;<tt>&lt;=</tt>&nbsp;<i>n</i>&nbsp;<tt>&lt;=</tt>&nbsp;7)</td>
 *     <td headers="matches">-</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\0</tt><i>mnn</i></td>
 *     <td headers="matches">The character with octal value <tt>0</tt><i>mnn</i>
 *         (0&nbsp;<tt>&lt;=</tt>&nbsp;<i>m</i>&nbsp;<tt>&lt;=</tt>&nbsp;3,
 *         0&nbsp;<tt>&lt;=</tt>&nbsp;<i>n</i>&nbsp;<tt>&lt;=</tt>&nbsp;7)</td>
 *     <td headers="matches">-</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\x</tt><i>hh</i></td>
 *     <td headers="matches">The character with hexadecimal&nbsp;value&nbsp;<tt>0x</tt><i>hh</i></td>
 *     <td headers="matches">-</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>&#92;u</tt><i>hhhh</i></td>
 *     <td headers="matches">The character with hexadecimal&nbsp;value&nbsp;<tt>0x</tt><i>hhhh</i></td>
 *     <td headers="matches">-</td></tr>
 * <tr><td valign="top" headers="matches"><tt>\t</tt></td>
 *     <td headers="matches">The tab character (<tt>'&#92;u0009'</tt>)</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\n</tt></td>
 *     <td headers="matches">The newline (line feed) character (<tt>'&#92;u000A'</tt>)</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\r</tt></td>
 *     <td headers="matches">The carriage-return character (<tt>'&#92;u000D'</tt>)</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\f</tt></td>
 *     <td headers="matches">The form-feed character (<tt>'&#92;u000C'</tt>)</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\a</tt></td>
 *     <td headers="matches">The alert (bell) character (<tt>'&#92;u0007'</tt>)</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\e</tt></td>
 *     <td headers="matches">The escape character (<tt>'&#92;u001B'</tt>)</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct characters"><tt>\c</tt><i>x</i></td>
 *     <td headers="matches">The control character corresponding to <i>x</i></td>
 *     <td headers="matches">-</td></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="3" id="classes">Character classes</th></tr>
 *
 * <tr><td valign="top" headers="construct classes"><tt>[abc]</tt></td>
 *     <td headers="matches"><tt>a</tt>, <tt>b</tt>, or <tt>c</tt> (simple class)</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct classes"><tt>[^abc]</tt></td>
 *     <td headers="matches">Any character except <tt>a</tt>, <tt>b</tt>, or <tt>c</tt> (negation)</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct classes"><tt>[a-zA-Z]</tt></td>
 *     <td headers="matches"><tt>a</tt> through <tt>z</tt>
 *         or <tt>A</tt> through <tt>Z</tt>, inclusive (range)</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct classes"><tt>[a-d[m-p]]</tt></td>
 *     <td headers="matches"><tt>a</tt> through <tt>d</tt>,
 *      or <tt>m</tt> through <tt>p</tt>: <tt>[a-dm-p]</tt> (union)</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct classes"><tt>[a-z&&[def]]</tt></td>
 *     <td headers="matches"><tt>d</tt>, <tt>e</tt>, or <tt>f</tt> (intersection)</td>
 *     <td headers="matches">-</td></tr>
 * <tr><td valign="top" headers="construct classes"><tt>[a-z&&[^bc]]</tt></td>
 *     <td headers="matches"><tt>a</tt> through <tt>z</tt>,
 *         except for <tt>b</tt> and <tt>c</tt>: <tt>[ad-z]</tt> (subtraction)</td>
 *     <td headers="matches">-</td></tr>
 * <tr><td valign="top" headers="construct classes"><tt>[a-z&&[^m-p]]</tt></td>
 *     <td headers="matches"><tt>a</tt> through <tt>z</tt>,
 *          and not <tt>m</tt> through <tt>p</tt>: <tt>[a-lq-z]</tt>(subtraction)</td>
 *     <td headers="matches">-</td></tr>
 * <tr><th>&nbsp;</th></tr>
 *
 * <tr align="left"><th colspan="2" id="predef">Predefined character classes</th></tr>
 *
 * <tr><td valign="top" headers="construct predef"><tt>.</tt></td>
 *     <td headers="matches">Any character (match <a href="#lt">line terminators</a>)</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct predef"><tt>\d</tt></td>
 *     <td headers="matches">A digit: <tt>[0-9]</tt></td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct predef"><tt>\D</tt></td>
 *     <td headers="matches">A non-digit: <tt>[^0-9]</tt></td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct predef"><tt>\s</tt></td>
 *     <td headers="matches">A whitespace character: <tt>[ \t\n\x0B\f\r]</tt></td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct predef"><tt>\S</tt></td>
 *     <td headers="matches">A non-whitespace character: <tt>[^\s]</tt></td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct predef"><tt>\w</tt></td>
 *     <td headers="matches">A word character: <tt>[a-zA-Z_0-9]</tt></td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct predef"><tt>\W</tt></td>
 *     <td headers="matches">A non-word character: <tt>[^\w]</tt></td>
 *     <td headers="matches">+</td></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="3" id="posix">POSIX character classes</b> - not supported<b></th></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="3">java.lang.Character classes - not supported</th></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="3" id="unicode">Classes for Unicode blocks and categories - not supported</th></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="3" id="bounds">Boundary matchers - not supported</th></tr>

 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="greedy">Greedy quantifiers</th></tr>
 *
 * <tr><td valign="top" headers="construct greedy"><i>X</i><tt>?</tt></td>
 *     <td headers="matches"><i>X</i>, once or not at all</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct greedy"><i>X</i><tt>*</tt></td>
 *     <td headers="matches"><i>X</i>, zero or more times</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct greedy"><i>X</i><tt>+</tt></td>
 *     <td headers="matches"><i>X</i>, one or more times</td>
 *     <td headers="matches">+</td></tr>
 * <tr><td valign="top" headers="construct greedy"><i>X</i><tt>{</tt><i>n</i><tt>}</tt></td>
 *     <td headers="matches"><i>X</i>, exactly <i>n</i> times</td>
 *     <td headers="matches">+</td></tr></tr>
 * <tr><td valign="top" headers="construct greedy"><i>X</i><tt>{</tt><i>n</i><tt>,}</tt></td>
 *     <td headers="matches"><i>X</i>, at least <i>n</i> times</td>
 *     <td headers="matches">+</td></tr></tr>
 * <tr><td valign="top" headers="construct greedy"><i>X</i><tt>{</tt><i>n</i><tt>,</tt><i>m</i><tt>}</tt></td>
 *     <td headers="matches"><i>X</i>, at least <i>n</i> but not more than <i>m</i> times</td>
 *     <td headers="matches">+</td></tr></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="3" id="reluc">Reluctant quantifiers - not supported</th></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="3" id="poss">Possessive quantifiers - not supported</th></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="3" id="logical">Logical operators</th></tr>
 *
 * <tr><td valign="top" headers="construct logical"><i>XY</i></td>
 *     <td headers="matches"><i>X</i> followed by <i>Y</i></td>
 *     <td headers="matches">+</td></tr></tr>
 * <tr><td valign="top" headers="construct logical"><i>X</i><tt>|</tt><i>Y</i></td>
 *     <td headers="matches">Either <i>X</i> or <i>Y</i></td>
 *     <td headers="matches">+</td></tr></tr>
 * <tr><td valign="top" headers="construct logical"><tt>(</tt><i>X</i><tt>)</tt></td>
 *     <td headers="matches">X, as a <a href="#cg">capturing group</a></td>
 *     <td headers="matches">+</td></tr></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="3" id="backref">Back references - not supported</th></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="3" id="quot">Quotation - not supported</th></tr>
 *
 * <tr><th>&nbsp;</th></tr>
 * <tr align="left"><th colspan="2" id="special">Special constructs (non-capturing) - not supported</th></tr>
 *
 * </table>
 */
public final class Matcher {

    private Matcher() {}

    /**
     * Check if string matches pattern
     * @param str string to check
     * @param pattern pattern to match
     * @return true if string matches pattern, otherwise - otherwise
     * @throws LexerImpl.LexerException if there are errors while tokenize pattern
     * @throws ParserImpl.ParserException if there are error in the syntax of pattern
     */
    public static boolean matches(String str, String pattern) {
        Parser parser = new ParserImpl(new LexerImpl(pattern));
        AST ast = parser.parse();
        State first = ast.visit(new NFABuilder());
        return NFA.emulate(str, first);
    }

}
