package poker.token.helpers;

import java.util.Map;

import poker.game.exception.ExcessiveBetException;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.exception.InvalidTokenValueException;
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
	
	public static TokenSet tokenSetForBet2(int amount, TokenValueDefinition tokenValueDefinition, TokenSet playerTokenSet) throws ExcessiveBetException, InvalidTokenAmountException {
		
		if(amount > evaluateTokenSetValue(tokenValueDefinition, playerTokenSet)){
			throw new ExcessiveBetException();
		}
		
		TokenSet betTokenSet = new TokenSet();
		
		int remaining = amount;
		int optAmount = 0;
		int effectiveAmount = 0;
		int totalAmount = 0;
		
		TokenType tokenTypeList[] = TokenType.values();
		
		for(int i = tokenTypeList.length - 1; i >= 0 && remaining > 0; i--){
			
			TokenType tokenType = tokenTypeList[i];
			
			if(!playerTokenSet.getTokensAmount().containsKey(tokenType))
				continue;
			
			int playerOwnedAmount = playerTokenSet.getAmountForTokenType(tokenType);
			optAmount = remaining / tokenValueDefinition.getValueForTokenType(tokenType);
			
			effectiveAmount = (optAmount > playerOwnedAmount) ? playerOwnedAmount : optAmount;

			remaining -= effectiveAmount * tokenValueDefinition.getValueForTokenType(tokenType);
			
			totalAmount = betTokenSet.getAmountForTokenType(tokenType) + effectiveAmount;
			betTokenSet.setAmountForTokenType(tokenType, totalAmount);
		}
		
		if(remaining > 0){
			for(TokenType tt : TokenType.values()){

				if(!playerTokenSet.getTokensAmount().containsKey(tt))
					continue;
				
				int playerOwnedAmount = playerTokenSet.getAmountForTokenType(tt);
				
				optAmount = (int) Math.ceil(remaining / (float)tokenValueDefinition.getValueForTokenType(tt));
				effectiveAmount = (optAmount > playerOwnedAmount) ? playerOwnedAmount : optAmount;
			
				remaining -= effectiveAmount * tokenValueDefinition.getValueForTokenType(tt);
			
				betTokenSet.increaseAmountForTokenType(tt, effectiveAmount);
			}
		}
		
		return betTokenSet;
	}
	
	private static TokenSet subTokenSetForBet(int amount, TokenValueDefinition tokenValueDefinition, TokenSet playerTokenSet, TokenSet betTokenSet) throws ExcessiveBetException, InvalidTokenAmountException {
		
		if(amount <= 0)
			return betTokenSet;
		
		Map<TokenType, Integer> tokenAmounts = playerTokenSet.getTokensAmount();
		
		for(TokenType tt : TokenType.values()) {
			if(tokenAmounts.containsKey(tt) && tokenAmounts.get(tt) > 0) {
				//Removing one token
				
				Integer tokenCount = tokenAmounts.get(tt);
				
				amount -= tokenValueDefinition.getValueForTokenType(tt);
				tokenAmounts.put(tt, tokenCount - 1);
				
				betTokenSet.setAmountForTokenType(tt, betTokenSet.getAmountForTokenType(tt) +1);
				
				return subTokenSetForBet(amount, tokenValueDefinition, playerTokenSet, betTokenSet);
			}		
		}
		return playerTokenSet;
	}
	
	public static TokenSet tokenSetForBet(int amount, TokenValueDefinition tokenValueDefinition, TokenSet playerTokenSet) throws ExcessiveBetException, InvalidTokenAmountException {
		if(amount > evaluateTokenSetValue(tokenValueDefinition, playerTokenSet)){
			throw new ExcessiveBetException();
		}
		
		TokenSet betTokenSet = new TokenSet();
		
		int amountCopy = amount;
		
		subTokenSetForBet(amountCopy, tokenValueDefinition, playerTokenSet.clone(), betTokenSet);
		
		return betTokenSet;
	}
	
	public static TokenSet tokenSetFromAmount(int amount, TokenValueDefinition tokenValueDefinition) {
		TokenSet tokenSet = new TokenSet();
		
		TokenType tokenType;
		try {
			tokenType = tokenValueDefinition.getTokenTypeForValue(tokenValueDefinition.getMinimumTokenValue());
			tokenSet.setAmountForTokenType(tokenType, amount);

		} catch (InvalidTokenValueException | InvalidTokenAmountException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return tokenSet;
	}
}
