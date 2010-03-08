package nd.regex;

import java.util.List;

/**
 *
 */
interface AST {

    Token token();

    void addChild(AST child);

    List<AST> children();

    AST removeLastChild();

    <E> E visit(ASTNodeVisitor<E> visitor);
}
