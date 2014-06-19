package poker.game.player.model;

import jade.core.AID;
import poker.card.heuristics.combination.model.Hand;

public class WinnerPlayer {

	private AID playerAID;
	private Hand winningHand;
	
	public WinnerPlayer(){}
	
	public WinnerPlayer(AID playerAID, Hand winningHand){
		this.playerAID = playerAID;
		this.winningHand = winningHand;
	}

	public AID getPlayerAID() {
		return playerAID;
	}

	public void setPlayerAID(AID playerAID) {
		this.playerAID = playerAID;
	}

	public Hand getWinningHand() {
		return winningHand;
	}

	public void setWinningHand(Hand winningHand) {
		this.winningHand = winningHand;
	}
}
