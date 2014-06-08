package sma.message.environment.notification;

import sma.message.Message;
import sma.message.MessageVisitor;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class PlayerFoldedNotification extends Message {

	private AID playerAID;
;
	
	public PlayerFoldedNotification(){}
	
	public PlayerFoldedNotification(AID playerAID){
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
		return visitor.onPlayerFoldedNotification(this, aclMsg);
	}
	
}
