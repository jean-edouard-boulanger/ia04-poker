package poker.game.management;

import java.util.List;

import poker.card.model.GameDeck;
import poker.card.model.UserDeck;
import poker.game.management.exception.ExcessiveBetException;
import poker.game.model.Game;
import poker.game.operations.BetOperation;
import poker.game.operations.Operation;
import poker.game.operations.exception.OperationFailureException;
import poker.game.player.model.Player;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.helpers.TokenSetValueEvaluator;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import poker.token.model.TokenValueDefinition;

public class GameManager {
	
	private static GameManager instance;
	
	private Game game = null;
	private Player player = null;

	public static GameManager getInstance(){
		if(instance == null){
			instance = new GameManager();
		}
		return instance;
	}
	
	private GameManager(){}

	public BetOperation placeBet(int amount) throws ExcessiveBetException, InvalidTokenAmountException{
		BetOperation betOperation = new BetOperation();
		betOperation.setEffectiveBetAmount(amount);
		
		TokenValueDefinition tokenValueDefinition = this.game.getTokenValueDefinition();
		
		if(amount > this.player.getBankroll(tokenValueDefinition)){
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
			effectiveAmount = optAmount - this.player.getTokens().getAmountForTokenType(tt);
			
			betTokenSet.setAmountForTokenType(tt, effectiveAmount);
			
			remaining -= effectiveAmount;
		}
		
		if(remaining > 0){
			int totalAmount = 0;
			TokenType tokenTypeList[] = TokenType.values();
			for(int i = tokenTypeList.length - 1; i >= 0 && remaining > 0; i--){
				
				optAmount = (int) Math.ceil(remaining / (float)tokenValueDefinition.getValueForTokenType(tokenTypeList[i]));
				effectiveAmount = optAmount - this.player.getTokens().getAmountForTokenType(tokenTypeList[i]);
		
				remaining -= effectiveAmount;
				
				totalAmount = betTokenSet.getAmountForTokenType(tokenTypeList[i]) + effectiveAmount;
				betTokenSet.setAmountForTokenType(tokenTypeList[i], totalAmount);
			}
		}
		
		betOperation.setBetTokenSet(betTokenSet);
		
		return betOperation;
	}
	
	public void handleOperation(Operation op) throws OperationFailureException{
		op.applyOperationToTokenSet(this.player.getTokens());
	}
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}