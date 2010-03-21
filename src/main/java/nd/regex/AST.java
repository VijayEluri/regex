package nd.regex;

import java.util.List;

/**
 * Abstract syntax tree.
 */
interface AST {

    Token token();

    void addChild(AST child);

    List<AST> children();

    AST removeLastChild();

    <E> E visit(ASTVisitor<E> visitor);
}
