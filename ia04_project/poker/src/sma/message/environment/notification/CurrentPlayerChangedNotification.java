package sma.message.environment.notification;

import sma.message.Message;
import sma.message.MessageVisitor;
import jade.lang.acl.ACLMessage;

public class CurrentPlayerChangedNotification extends Message {

	private int playerTablePositionIndex;
	
	public CurrentPlayerChangedNotification(){}
	
	public CurrentPlayerChangedNotification(int playerTablePositionIndex){
		this.playerTablePositionIndex = playerTablePositionIndex;
	}

	public int getPlayerTablePositionIndex() {
		return playerTablePositionIndex;
	}

	public void setPlayerTablePositionIndex(int playerTablePositionIndex) {
		this.playerTablePositionIndex = playerTablePositionIndex;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onCurrentPlayerChangedNotification(this, aclMsg);
	}
	
}