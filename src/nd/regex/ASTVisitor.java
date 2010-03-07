package nd.regex;

/**
 *
 */
interface ASTVisitor<E> {

    E visit(AST ast);

}
