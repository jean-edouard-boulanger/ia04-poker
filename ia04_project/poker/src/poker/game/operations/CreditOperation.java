package poker.game.operations;

import poker.game.operations.exception.OperationFailureException;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.model.TokenSet;

public class CreditOperation extends Operation{
	private TokenSet creditedTokenSet = null;
	
	public CreditOperation(){}
	
	public CreditOperation(TokenSet creditedTokenSet){
		this.creditedTokenSet = creditedTokenSet;
	}
	
	public void setCreditedTokenSet(TokenSet creditedTokenSet){
		this.creditedTokenSet = creditedTokenSet;
	}
	
	public TokenSet getCreditedTokenSet(){
		return this.creditedTokenSet;
	}

	@Override
	public void applyOperationToTokenSet(TokenSet tokenSet) throws OperationFailureException {
		try {
			tokenSet.AddTokenSet(this.creditedTokenSet);
		} catch (InvalidTokenAmountException e) {
			throw new OperationFailureException();
		}
	}
}
