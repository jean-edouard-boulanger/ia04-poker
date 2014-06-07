package sma.message;

import jade.lang.acl.ACLMessage;

public class PlayerCheckNotification extends Message {

	private int playerTablePositionIndex;
	
	public PlayerCheckNotification(){}
	
	public PlayerCheckNotification(int playerTablePositionIndex){
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
		return visitor.onPlayerCheckNotification(this, aclMsg);
	}
}
