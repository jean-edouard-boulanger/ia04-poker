package gui.player.event.model;

import poker.token.model.TokenSet;

public class PlayerBetEventData {
	
	private TokenSet tokenSetUsedForBet;
	private int betAmount;
	private int amountAddedForBet;

	private int playerIndex;
	
	public PlayerBetEventData(TokenSet tokenSetUsedForBet, int betAmount, int playerIndex, int amountAddedForBet){
		this.betAmount = betAmount;
		this.tokenSetUsedForBet = tokenSetUsedForBet;
		this.playerIndex = playerIndex;
		this.amountAddedForBet = amountAddedForBet;
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
	
	public int getAmountAddedForBet() {
		return amountAddedForBet;
	}

	public void setAmountAddedForBet(int amountAddedForBet) {
		this.amountAddedForBet = amountAddedForBet;
	}

}