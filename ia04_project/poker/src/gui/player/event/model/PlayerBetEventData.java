package gui.player.event.model;

import poker.token.model.TokenSet;

public class PlayerBetEventData {
	
	private TokenSet tokenSetUsedForBet;
	private int betAmount;
	private int playerIndex;
	
	public PlayerBetEventData(TokenSet tokenSetUsedForBet, int betAmount, int playerIndex){
		this.betAmount = betAmount;
		this.tokenSetUsedForBet = tokenSetUsedForBet;
		this.playerIndex = playerIndex;
	}
	
	public int getBetAmount() {
		return betAmount;
	}

	public void setBetAmount(int betAmount) {
		this.betAmount = betAmount;
	}

	public PlayerBetEventData(){}

	public int getPlayerIndex() {
		return playerIndex;
	}

	public void setPlayerIndex(int playerIndex) {
		this.playerIndex = playerIndex;
	}
	
	public TokenSet getTokenSetUsedForBet() {
		return this.tokenSetUsedForBet;
	}
	
	public void setTokenSetUsedForBet(TokenSet betTokenSet) {
		this.tokenSetUsedForBet = betTokenSet;
		
	}

}