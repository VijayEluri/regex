package nd.regex;

/**
 * Lexer.
 * Transform sequence of characters to stream of tokens.
 */
interface Lexer {

    Token nextToken();

    boolean hasNext();

}
