package poker.game.operations;

import poker.game.operations.exception.OperationFailureException;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.model.TokenSet;

public abstract class Operation {
	
	public abstract void applyOperationToTokenSet(TokenSet tokenSet) throws OperationFailureException;
	
}
