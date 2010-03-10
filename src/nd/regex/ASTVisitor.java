package nd.regex;

/**
 *
 */
public interface ASTVisitor<E> {

    E visit(SequenceNode sequence);

    E visit(BoundedQuantifierNode quantifier);

    E visit(UnboundedQuantifierNode quantifier);

    E visit(CharacterClassNode charClass);

    E visit(CharacterClassIntervalNode interval);

    E visit(CharacterNode character);

    E visit(AlternativeNode alternative);
}
