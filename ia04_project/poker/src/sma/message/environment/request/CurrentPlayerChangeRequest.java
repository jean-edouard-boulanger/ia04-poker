package sma.message.environment.request;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class CurrentPlayerChangeRequest extends Message {

	private AID playerAID;
	
	public CurrentPlayerChangeRequest(){}
	
	public CurrentPlayerChangeRequest(AID playerAID){
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
		return visitor.onCurrentPlayerChangeRequest(this, aclMsg);
	}
}
