package sma.message;

import jade.lang.acl.ACLMessage;

public class PlayerFoldedNotification extends Message {

	private int playerTablePositionIndex;
	
	public PlayerFoldedNotification(){}
	
	public PlayerFoldedNotification(int playerTablePositionIndex){
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
		return visitor.onPlayerFoldedNotification(this, aclMsg);
	}
	
}
