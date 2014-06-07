package sma.message;

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
