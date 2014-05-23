package poker.token.factories;

import poker.token.exception.InvalidRepartitionException;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.model.TokenRepartition;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;

public class TokenSetFactory {

	public static TokenSet createTokenSet(TokenRepartition repartition, int nbTokens) throws InvalidTokenAmountException, InvalidRepartitionException{
		int totalRepartition = 0;
		TokenSet t = new TokenSet();
		
		for(TokenType tt : TokenType.values()){
			t.setAmountForTokenType(tt, nbTokens * repartition.getRepartitionForToken(tt) / 100);
			totalRepartition += repartition.getRepartitionForToken(tt);
		}
		
		if(totalRepartition != 100){
			throw new InvalidRepartitionException();
		}
		
		return t;
	}
	
}
