package poker.token.helpers;

import poker.game.exception.ExcessiveBetException;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import poker.token.model.TokenValueDefinition;

public class TokenSetValueEvaluator {
		
	private TokenSetValueEvaluator(){}
	
	public static int evaluateTokenSetValue(TokenValueDefinition valueDefinition, TokenSet tokenSet){
		if(tokenSet == null || valueDefinition == null){
			return 0;
		}
		
		int totalValue = 0;
		for(TokenType tt : TokenType.values()){
			totalValue += tokenSet.getAmountForTokenType(tt) * valueDefinition.getValueForTokenType(tt);
		}
		return totalValue;
	}
	
	public static TokenSet tokenSetForBet(int amount, TokenValueDefinition tokenValueDefinition, TokenSet playerTokenSet) throws ExcessiveBetException, InvalidTokenAmountException {
		
		if(amount > evaluateTokenSetValue(tokenValueDefinition, playerTokenSet)){
			throw new ExcessiveBetException();
		}
		
		TokenSet betTokenSet = new TokenSet();
		
		int remaining = amount;
		int optAmount = 0;
		int effectiveAmount = 0;
		
		for(TokenType tt : TokenType.values()){
			
			if(remaining == 0){
				break;
			}
			
			optAmount = remaining / tokenValueDefinition.getValueForTokenType(tt);
			effectiveAmount = optAmount - playerTokenSet.getAmountForTokenType(tt);
			
			betTokenSet.setAmountForTokenType(tt, effectiveAmount);
			
			remaining -= effectiveAmount;
		}
		
		if(remaining > 0){
			int totalAmount = 0;
			TokenType tokenTypeList[] = TokenType.values();
			for(int i = tokenTypeList.length - 1; i >= 0 && remaining > 0; i--){
				
				optAmount = (int) Math.ceil(remaining / (float)tokenValueDefinition.getValueForTokenType(tokenTypeList[i]));
				effectiveAmount = optAmount -playerTokenSet.getAmountForTokenType(tokenTypeList[i]);
		
				remaining -= effectiveAmount;
				
				totalAmount = betTokenSet.getAmountForTokenType(tokenTypeList[i]) + effectiveAmount;
				betTokenSet.setAmountForTokenType(tokenTypeList[i], totalAmount);
			}
		}
		
		return betTokenSet;
	}
}
