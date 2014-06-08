package sma.message.environment.notification;

import sma.message.Message;
import sma.message.MessageVisitor;
import jade.lang.acl.ACLMessage;

public class PlayerReceivedUnknownCardNotification extends Message {

	private int playerTablePositionIndex;
	
	public PlayerReceivedUnknownCardNotification(){}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPlayerReceivedUnknownCardNotification(this, aclMsg);
	}

	public int getPlayerTablePositionIndex(){
		return this.playerTablePositionIndex;
	}
	
	public void setPlayerTablePositionIndex(int index){
		this.playerTablePositionIndex = index;
	}	
}
