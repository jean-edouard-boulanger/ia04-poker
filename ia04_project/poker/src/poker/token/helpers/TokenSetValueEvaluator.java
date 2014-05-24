package poker.token.helpers;

import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import poker.token.model.TokenValueDefinition;

public class TokenSetValueEvaluator {
		
	private TokenSetValueEvaluator(){}
	
	public static int evaluateTokenSetValue(TokenValueDefinition valueDefinition, TokenSet tokenSet){
		int totalValue = 0;
		for(TokenType tt : TokenType.values()){
			totalValue += tokenSet.getAmountForTokenType(tt) * valueDefinition.getValueForTokenType(tt);
		}
		return totalValue;
	}
}
