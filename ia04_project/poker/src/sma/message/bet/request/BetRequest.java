package sma.message.bet.request;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class BetRequest extends Message {

	private int bet;
	private AID playerAID;
	
	public BetRequest(){}
	
	public BetRequest(int bet,AID player) {
		this.bet = bet;
		this.playerAID = player;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onBetRequest(this, aclMsg);
	}

	public int getBet() {
		return bet;
	}

	public void setBet(int bet) {
		this.bet = bet;
	}

	public AID getPlayerAID() {
		return playerAID;
	}

	public void setPlayerAID(AID player) {
		this.playerAID = player;
	}

}
