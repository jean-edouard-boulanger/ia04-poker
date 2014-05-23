package model.token;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe representant une collection de jetons de poker
 *
 */
public class TokenSet {

	private Map<TokenType, Integer> tokensAmount;
	
	public Map<TokenType, Integer> getTokensAmount(){
		return this.tokensAmount;
	}
	
	public void setMap(Map<TokenType, Integer> tokensAmount){
		this.tokensAmount = tokensAmount;
	}
	
	public TokenSet(){
		this.tokensAmount = new HashMap<TokenType, Integer>();
	}
	
	public TokenSet(Map<TokenType, Integer> tokensAmount){
		this.tokensAmount = tokensAmount;
	}
	
	public int getAmountForTokenType(TokenType tokenType){
		Integer amount = this.tokensAmount.get(tokenType);
		return (amount == null) ? 0 : amount;
	}
	
	public void setAmountForTokenType(TokenType tokenType, int amount){
		this.tokensAmount.put(tokenType, amount);
	}
	
	public void increaseAmountForTokenType(TokenType tokenType, int amount){
		int newAmount = this.tokensAmount.get(tokenType) + amount;
		this.tokensAmount.put(tokenType, newAmount);
	}
	
	public void decreaseAmountForTokenType(TokenType tokenType, int amount){
		int newAmount = this.tokensAmount.get(tokenType) - amount;
		this.tokensAmount.put(tokenType, newAmount);
	}
	
	public void AddTokenSet(TokenSet addedTokenSet){
		for(TokenType tt : TokenType.values()){
			this.increaseAmountForTokenType(tt, addedTokenSet.getAmountForTokenType(tt));
		}
	}	
	
	public void SubstractTokenSet(TokenSet substractedTokenSet){
		for(TokenType tt : TokenType.values()){
			this.decreaseAmountForTokenType(tt, substractedTokenSet.getAmountForTokenType(tt));
		}
	}
	
	public void reset(){
		for(TokenType tt : TokenType.values()){
			this.setAmountForTokenType(tt, 0);
		}
	}
}
