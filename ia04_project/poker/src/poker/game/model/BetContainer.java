package poker.game.model;

import jade.core.AID;

import java.util.HashMap;

import poker.game.player.model.Player;
import poker.token.helpers.TokenSetValueEvaluator;
import poker.token.model.TokenSet;
import poker.token.model.TokenValueDefinition;

public class BetContainer {

	TokenValueDefinition tokenValueDefinition;
	private int currentBetAmount = 0;
	private TokenSet pot;
	private HashMap<AID, TokenSet> playersBets;
	
	public BetContainer(){
		this.tokenValueDefinition = new TokenValueDefinition();
		this.pot = new TokenSet();
		this.playersBets = new HashMap<AID, TokenSet>();
	}

	public TokenValueDefinition getTokenValueDefinition() {
		return tokenValueDefinition;
	}

	public void setTokenValueDefinition(TokenValueDefinition tokenValueDefinition) {
		this.tokenValueDefinition = tokenValueDefinition;
	}

	public int getCurrentBetAmount() {
		return currentBetAmount;
	}

	public void setCurrentBetAmount(int currentBetAmount) {
		this.currentBetAmount = currentBetAmount;
	}

	public TokenSet getPot() {
		return pot;
	}

	public int getPotAmount(){
		return TokenSetValueEvaluator.evaluateTokenSetValue(this.tokenValueDefinition, this.pot);
	}
	
	public void setPot(TokenSet pot) {
		this.pot = pot;
	}

	public HashMap<AID, TokenSet> getPlayersBets() {
		return playersBets;
	}

	public void setPlayersBets(HashMap<AID, TokenSet> playersBets) {
		this.playersBets = playersBets;
	}	
	
	public void setPlayerCurrentBet(AID playerAID, TokenSet bet){
		this.playersBets.put(playerAID, bet);
	}
	
	public TokenSet getPlayerCurrentBet(Player p){
		return this.playersBets.get(p);
	}
	
	public int getPlayerCurrentBetAmount(Player p){
		return TokenSetValueEvaluator.evaluateTokenSetValue(this.tokenValueDefinition, this.playersBets.get(p));
	}
	
	public TokenSet getGlobalCurrentBet(){
		TokenSet gSet = new TokenSet();
		for(TokenSet t : this.playersBets.values()){
			gSet.AddTokenSet(t);
		}
		return gSet;
	}
	
	public int getGlobalCurrentBetAmount(){
		return TokenSetValueEvaluator.evaluateTokenSetValue(this.tokenValueDefinition, this.getGlobalCurrentBet());
	}
	
	public TokenSet transferCurrentBetsToPot(){
		for(TokenSet tokenSet : this.playersBets.values()){
			this.pot.AddTokenSet(tokenSet);
		}
		this.playersBets.clear();
		
		return this.pot;
	}
	
	public TokenSet popPot(){
		TokenSet tmpPot = new TokenSet(this.pot);
		this.pot.clear();
		return tmpPot;
	}
}
