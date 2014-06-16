package poker.game.operations;

import poker.game.operations.exception.OperationFailureException;
import poker.token.exception.NotEnoughTokenException;
import poker.token.model.TokenSet;

public class DebitOperation extends BankRollOperation {

	private TokenSet debittedTokenSet = null;
	
	public DebitOperation(){}
	
	public DebitOperation(TokenSet tokenSet){
		this.debittedTokenSet = tokenSet;
	}
	
	public void setDebittedTokenSet(TokenSet debittedTokenSet){
		this.debittedTokenSet = debittedTokenSet;
	}
	
	public TokenSet getDebittedTokenSet(){
		return this.debittedTokenSet;
	}

	@Override
	public void ApplyOperationToTokenSet(TokenSet tokenSet) throws OperationFailureException {
		try{
			tokenSet.substractTokenSet(this.debittedTokenSet);
		}
		catch(Exception e){
			throw new OperationFailureException();
		}
	}
}
