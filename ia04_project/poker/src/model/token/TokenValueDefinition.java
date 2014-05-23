package model.token;

import java.util.HashMap;
import java.util.Map;

import model.token.exception.InvalidTokenValueException;

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
	
	public void setTokenValues(Map<TokenType, Integer> tokenValues){
		this.tokenValues = tokenValues;
	}
}
