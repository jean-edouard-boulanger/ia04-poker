package poker.game.operations;

import poker.game.operations.exception.OperationFailureException;
import poker.token.model.TokenSet;

public abstract class BankRollOperation {
	
	public abstract void ApplyOperationToTokenSet(TokenSet tokenSet) throws OperationFailureException;
	
}