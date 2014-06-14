package poker.game.model;

import poker.token.exception.InvalidBlindException;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import poker.token.model.TokenValueDefinition;

public class BlindValueDefinition {
	private int blindAmountDefinition;
	
	public BlindValueDefinition(){
		this.blindAmountDefinition = 0;
	}
	
	public BlindValueDefinition(TokenValueDefinition tokenValueDefinition){
		this.blindAmountDefinition = tokenValueDefinition.getMinimumTokenValue();
	}
	
	public BlindValueDefinition(int blindAmountDefinition, TokenValueDefinition tokenValueDefinition){
		setBlindAmountDefinition(blindAmountDefinition, tokenValueDefinition);
	}
	
	public void setBlindAmountDefinition(int blindAmountDefinition, TokenValueDefinition tokenValueDefinition) {
		int minimumValue = tokenValueDefinition.getMinimumTokenValue();
		
		if(blindAmountDefinition % minimumValue == 0) {
			this.blindAmountDefinition = blindAmountDefinition;
		}
		else {
			try {
				throw new InvalidBlindException();
			} catch (InvalidBlindException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public int getBlindAmountDefinition(){
		return this.blindAmountDefinition;
	}
	
	public int getBigBlindAmountDefinition(){
		return this.blindAmountDefinition * 2;
	}
	
	public void increase() {
		blindAmountDefinition *= 2;
	}
}
