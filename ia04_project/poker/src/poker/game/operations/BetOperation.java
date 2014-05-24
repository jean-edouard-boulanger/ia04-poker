package poker.game.operations;

import poker.game.operations.exception.OperationFailureException;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.model.TokenSet;

public class BetOperation extends Operation {
	private int effectiveBetAmount = 0;
	private TokenSet betTokenSet = null;
	
	public BetOperation(){}
	
	public BetOperation(int effectiveBetAmount, TokenSet betTokenSet){
		this.effectiveBetAmount = effectiveBetAmount;
		this.betTokenSet = betTokenSet;
	}
	
	public TokenSet getBetTokenSet(){
		return this.betTokenSet;
	}
	
	public void setBetTokenSet(TokenSet betTokenSet){
		this.betTokenSet = betTokenSet;
	}	
	
	public int getEffectiveBetAmount(){
		return this.effectiveBetAmount;
	}
	
	public void setEffectiveBetAmount(int effectiveBetAmount){
		this.effectiveBetAmount = effectiveBetAmount;
	}
	
	@Override
	public void applyOperationToTokenSet(TokenSet tokenSet) throws OperationFailureException {
		try {
			tokenSet.SubstractTokenSet(this.betTokenSet);
		} catch (InvalidTokenAmountException e) {
			throw new OperationFailureException();
		}
	}
}
