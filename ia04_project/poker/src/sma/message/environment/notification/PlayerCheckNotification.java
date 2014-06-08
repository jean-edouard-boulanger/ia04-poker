package sma.message.environment.notification;

import sma.message.Message;
import sma.message.MessageVisitor;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class PlayerCheckNotification extends Message {

	private AID playerAID;
	
	public PlayerCheckNotification(){}
	
	public PlayerCheckNotification(AID playerAID){
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
		return visitor.onPlayerCheckNotification(this, aclMsg);
	}
}
