package sma.message.environment.request;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import poker.token.model.TokenSet;
import sma.message.Message;
import sma.message.MessageVisitor;

public class PlayerBetRequest extends Message {

	private TokenSet bet;
	private AID playerAID;
	int betAmount;
	
	public PlayerBetRequest(){}
	
	public PlayerBetRequest(TokenSet bet,AID player, int betAmount) {
		this.bet = bet;
		this.playerAID = player;
		this.betAmount = betAmount;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPlayerBetRequest(this, aclMsg);
	}

	public TokenSet getBet() {
		return bet;
	}

	public void setBet(TokenSet bet) {
		this.bet = bet;
	}

	public AID getPlayerAID() {
		return playerAID;
	}

	public void setPlayerAID(AID player) {
		this.playerAID = player;
	}

	public int getBetAmount() {
		return betAmount;
	}

	public void setBetAmount(int betAmount) {
		this.betAmount = betAmount;
	}

}
