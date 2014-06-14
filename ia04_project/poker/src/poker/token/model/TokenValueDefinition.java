package poker.token.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import poker.token.exception.InvalidTokenValueException;

public class TokenValueDefinition {
	
	private Map<TokenType, Integer> tokenValues;
	
	public TokenValueDefinition(){
		this.tokenValues = new HashMap<TokenType, Integer>();
	}
	
	public TokenValueDefinition(Map<TokenType, Integer> tokenValue){
		this.tokenValues = tokenValue;
	}
	
	public void setValueForTokenType(TokenType tokenType, int value) throws InvalidTokenValueException{
		if(value < 0){
			throw new InvalidTokenValueException(value);
		}
		this.tokenValues.put(tokenType, value);
	}
	
	public int getValueForTokenType(TokenType tt){
		Integer value = this.tokenValues.get(tt);
		return (value == null) ? 0 : value;
	}
	
	public Map<TokenType, Integer> getTokenValues() {
		return tokenValues;
	}
	
	public void setTokenValues(Map<TokenType, Integer> tokenValues){
		this.tokenValues = tokenValues;
	}
	
	@JsonIgnore 
	public Integer getMinimumTokenValue() {
		int min = tokenValues.get(TokenType.BLACK);
		
		for(TokenType t : tokenValues.keySet()) {
			if(min > tokenValues.get(t)) {
				min = tokenValues.get(t);
			}
		}
		
		return min;
	} 
}
