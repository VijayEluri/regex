package nd.regex;

/**
 *
 */
interface Lexer {

    Token nextToken();

    boolean hasNext();

}
