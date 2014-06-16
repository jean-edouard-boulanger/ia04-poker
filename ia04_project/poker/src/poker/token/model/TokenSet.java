package poker.token.model;

import java.util.HashMap;
import java.util.Map;

import poker.token.exception.InvalidTokenAmountException;
import poker.token.helpers.TokenSetValueEvaluator;

/**
 * Classe representant une collection de jetons de poker
 *
 */
public class TokenSet {

	private Map<TokenType, Integer> tokensAmount;
	
	public TokenSet(){
		this.tokensAmount = new HashMap<TokenType, Integer>();
	}
	
	public TokenSet(TokenSet set){
		this.tokensAmount = new HashMap<TokenType, Integer>(set.tokensAmount);
	}
	
	public TokenSet(Map<TokenType, Integer> tokensAmount){
		this.tokensAmount = tokensAmount;
	}
	
	public Map<TokenType, Integer> getTokensAmount(){
		return this.tokensAmount;
	}
	
	public void setTokensAmount(Map<TokenType, Integer> tokensAmount){
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
		if(this.tokensAmount.containsKey(tokenType)){
			int newAmount = this.tokensAmount.get(tokenType) + amount;
			this.tokensAmount.put(tokenType, newAmount);
		}
		else
			this.tokensAmount.put(tokenType, amount);
		
	}
	
	public void decreaseAmountForTokenType(TokenType tokenType, int amount) throws InvalidTokenAmountException{
		if(amount < 0){
			throw new InvalidTokenAmountException();
		}
		int newAmount = this.tokensAmount.get(tokenType) - amount;
		this.setAmountForTokenType(tokenType, newAmount);
	}
	
	public TokenSet addTokenSet(TokenSet addedTokenSet){
		int newValue = 0;
		
		for(Map.Entry<TokenType, Integer> entry : addedTokenSet.getTokensAmount().entrySet()) {
			if(!this.tokensAmount.containsKey(entry.getKey())) {
				this.tokensAmount.put(entry.getKey(), entry.getValue());
			}
			else {
				newValue = this.getAmountForTokenType(entry.getKey()) + entry.getValue();
				this.tokensAmount.put(entry.getKey(), newValue);
			}
		}
		
		return this;
	}	
	
	public TokenSet substractTokenSet(TokenSet substractedTokenSet) throws InvalidTokenAmountException{
		Map<TokenType, Integer> tmpTokensAmount = new HashMap<TokenType, Integer>(this.tokensAmount);
		
		int newValue = 0;
		for(Map.Entry<TokenType, Integer> entry : substractedTokenSet.getTokensAmount().entrySet()){
			newValue = this.getAmountForTokenType(entry.getKey()) - entry.getValue();
			tmpTokensAmount.put(entry.getKey(), newValue);
			
			if(newValue < 0){
				throw new InvalidTokenAmountException();
			}
		}
		
		this.tokensAmount = tmpTokensAmount;
		
		return this;
	}
	
	public void clear(){
		for(TokenType tt : TokenType.values()){
			this.tokensAmount.put(tt, 0);
		}
	}
	
	public String toString() {
		String tokenSet = "TokenSet\n";
		
		int value = 0;
		
		for(TokenType tt : TokenType.values()){
			tokenSet += tt + " : " + this.getAmountForTokenType(tt) + "\n";
		}
		
		return tokenSet;
	}
}
