package sma.message.bet.request;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class DoesPlayerHaveToBetRequest extends Message {

	AID playerAID;
	
	public DoesPlayerHaveToBetRequest(){}
	
	public DoesPlayerHaveToBetRequest(AID playerAID){
		this.playerAID = playerAID;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onDoesPlayerHaveToBetRequest(this, aclMsg);
	}

	public AID getPlayerAID() {
		return playerAID;
	}

	public void setPlayerAID(AID playerAID) {
		this.playerAID = playerAID;
	}
	
}
