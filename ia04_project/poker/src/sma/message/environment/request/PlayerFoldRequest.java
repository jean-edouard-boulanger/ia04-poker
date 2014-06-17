package sma.message.environment.request;

import sma.message.Message;
import sma.message.MessageVisitor;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class PlayerFoldRequest extends Message {

	AID playerAID;
	
	public PlayerFoldRequest(){}
	
	public PlayerFoldRequest(AID playerAID){
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
		return visitor.onPlayerFoldRequest(this, aclMsg);
	}
}
