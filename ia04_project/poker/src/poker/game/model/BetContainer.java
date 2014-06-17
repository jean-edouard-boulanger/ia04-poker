package poker.game.model;

import jade.core.AID;

import java.util.HashMap;

import poker.game.player.model.Player;
import poker.token.helpers.TokenSetValueEvaluator;
import poker.token.model.TokenSet;
import poker.token.model.TokenValueDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BetContainer {

	TokenValueDefinition tokenValueDefinition;
	

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
	
	public void clearPlayerBets() {
		playersBets.clear();
	}
	
	public void clearPot() {
		pot.clear();
	}
	
	public void setTokenValueDefinition(TokenValueDefinition tokenValueDefinition) {
		this.tokenValueDefinition = tokenValueDefinition;
	}

	@JsonIgnore
	public int getCurrentBetAmount() {
		int betAmount = 0;
		for(TokenSet bet : playersBets.values()){
			int curAmount = TokenSetValueEvaluator.evaluateTokenSetValue(this.tokenValueDefinition, bet);
			if(curAmount > betAmount)
				betAmount = curAmount;
		}
		return betAmount;
	}

	public TokenSet getPot() {
		return pot;
	}

	@JsonIgnore
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
	
	public void addTokenToPlayerBet(AID playerAID, TokenSet betToAdd){
		if(this.playersBets.containsKey(playerAID))
			this.playersBets.put(playerAID, this.playersBets.get(playerAID).addTokenSet(betToAdd));
		else
			this.playersBets.put(playerAID, betToAdd);
	}
	
	public void setPlayerCurrentBet(AID playerAID, TokenSet bet){
		this.playersBets.put(playerAID, bet);
	}

	public TokenSet getPlayerCurrentBet(Player p){
		return this.playersBets.get(p.getAID());
	}

	public int getPlayerCurrentBetAmount(Player p){
		return TokenSetValueEvaluator.evaluateTokenSetValue(this.tokenValueDefinition, this.playersBets.get(p.getAID()));
	}

	@JsonIgnore
	public TokenSet getGlobalCurrentBet(){
		TokenSet gSet = new TokenSet();
		for(TokenSet t : this.playersBets.values()){
			gSet.addTokenSet(t);
		}
		return gSet;
	}

	@JsonIgnore
	public int getGlobalCurrentBetAmount(){
		return TokenSetValueEvaluator.evaluateTokenSetValue(this.tokenValueDefinition, this.getGlobalCurrentBet());
	}

	public TokenSet transferCurrentBetsToPot(){
		for(TokenSet tokenSet : this.playersBets.values()){
			this.pot.addTokenSet(tokenSet);
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
