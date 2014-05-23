package poker.token.model;

import java.util.HashMap;
import java.util.Map;

import poker.token.exception.InvalidRepartitionException;

public class TokenRepartition {
	private Map<TokenType, Integer> tokenRepartition;

	public TokenRepartition(){
		this.tokenRepartition = new HashMap<TokenType, Integer>();
		for(TokenType tt : TokenType.values()){
			this.tokenRepartition.put(tt, 20);
		}
	}
	
	public TokenRepartition(Map<TokenType, Integer> tokenRepartition){
		this.tokenRepartition = tokenRepartition;
	}
	
	public TokenRepartition(int whiteRepartition, int redRepartition, int greenRepartition, int blueRepartition, int blackRepartition){
		this.tokenRepartition.put(TokenType.WHITE, whiteRepartition);
		this.tokenRepartition.put(TokenType.RED, redRepartition);
		this.tokenRepartition.put(TokenType.GREEN, greenRepartition);
		this.tokenRepartition.put(TokenType.BLUE, blueRepartition);
		this.tokenRepartition.put(TokenType.BLACK, blackRepartition);
	}
	
	public void setRepartitionForToken(TokenType tokenType, int repartition) throws InvalidRepartitionException{
		if(repartition < 0){
			throw new InvalidRepartitionException();
		}
		this.tokenRepartition.put(tokenType, repartition);
	}
	
	public int getRepartitionForToken(TokenType tokenType){
		Integer repartition = this.tokenRepartition.get(tokenType);
		return (repartition == null) ? 0 : repartition;
	}
}
