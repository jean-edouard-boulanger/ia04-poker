package sma.message;

import poker.card.model.Card;
import jade.lang.acl.ACLMessage;

public class PlayerReceivedCardNotification extends Message {

	private int playerTablePositionIndex;
	private Card receivedCard;
	
	public PlayerReceivedCardNotification(){}
	public PlayerReceivedCardNotification(int playerTablePositionIndex, int receivedCard){
		
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPlayerReceivedCardNotification(this, aclMsg);
	}

	public int getPlayerTablePositionIndex(){
		return this.playerTablePositionIndex;
	}
	
	public void setPlayerTablePositionIndex(int index){
		this.playerTablePositionIndex = index;
	}
	
	
}
