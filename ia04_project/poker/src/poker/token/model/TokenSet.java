package poker.token.model;

import java.util.HashMap;
import java.util.Map;

import poker.token.exception.InvalidTokenAmountException;

/**
 * Classe representant une collection de jetons de poker
 *
 */
public class TokenSet {

	private Map<TokenType, Integer> tokensAmount;
	
	public TokenSet(){
		this.tokensAmount = new HashMap<TokenType, Integer>();
	}
	
	public TokenSet(Map<TokenType, Integer> tokensAmount){
		this.tokensAmount = tokensAmount;
	}
	
	public Map<TokenType, Integer> getTokensAmount(){
		return this.tokensAmount;
	}
	
	public void setMap(Map<TokenType, Integer> tokensAmount){
		this.tokensAmount = tokensAmount;
	}
		
	public int getAmountForTokenType(TokenType tokenType){
		Integer amount = this.tokensAmount.get(tokenType);
		return (amount == null) ? 0 : amount;
	}
	
	public void setAmountForTokenType(TokenType tokenType, int amount) throws InvalidTokenAmountException{
		if(amount < 0){
			throw new InvalidTokenAmountException();
		}
		this.tokensAmount.put(tokenType, amount);
	}
	
	public void increaseAmountForTokenType(TokenType tokenType, int amount) throws InvalidTokenAmountException{
		if(amount < 0){
			throw new InvalidTokenAmountException();
		}
		int newAmount = this.tokensAmount.get(tokenType) + amount;
		this.tokensAmount.put(tokenType, newAmount);
	}
	
	public void decreaseAmountForTokenType(TokenType tokenType, int amount) throws InvalidTokenAmountException{
		if(amount < 0){
			throw new InvalidTokenAmountException();
		}
		int newAmount = this.tokensAmount.get(tokenType) - amount;
		this.setAmountForTokenType(tokenType, newAmount);
	}
	
	public void AddTokenSet(TokenSet addedTokenSet) throws InvalidTokenAmountException{
		for(TokenType tt : TokenType.values()){
			this.increaseAmountForTokenType(tt, addedTokenSet.getAmountForTokenType(tt));
		}
	}	
	
	public void SubstractTokenSet(TokenSet substractedTokenSet) throws InvalidTokenAmountException{
		for(TokenType tt : TokenType.values()){
			this.decreaseAmountForTokenType(tt, substractedTokenSet.getAmountForTokenType(tt));
		}
	}
	
	public void reset(){
		for(TokenType tt : TokenType.values()){
			this.tokensAmount.put(tt, 0);
		}
	}
}
