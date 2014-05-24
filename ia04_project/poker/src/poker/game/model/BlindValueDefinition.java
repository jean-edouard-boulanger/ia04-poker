package poker.game.model;

import poker.token.model.TokenSet;

public class BlindValueDefinition {
	private TokenSet blindAmountDefinition;
	
	public BlindValueDefinition(){
		this.blindAmountDefinition = new TokenSet();
	}
	
	public BlindValueDefinition(TokenSet blindAmountDefinition){
		this.blindAmountDefinition = blindAmountDefinition;
	}
	
	public void setBlindAmountDefinition(TokenSet blindAmountDefinition){
		this.blindAmountDefinition = blindAmountDefinition;
	}
	
	public TokenSet getBlindAmountDefinition(){
		return this.blindAmountDefinition;
	}
}
