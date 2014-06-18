package sma.message.environment.request;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class RevealPlayerCardsRequest extends Message {

	AID playerAID;
	
	public RevealPlayerCardsRequest(){}
	
	public RevealPlayerCardsRequest(AID playerAID) {
		this.playerAID = playerAID;
	}
	
	public AID getPlayerAID() {
		return playerAID;
	}

	public void setPlayerAID(AID playerAID) {
		this.playerAID = playerAID;
	}

	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onRevealPlayerCardsRequest(this, aclMsg);
	}
}