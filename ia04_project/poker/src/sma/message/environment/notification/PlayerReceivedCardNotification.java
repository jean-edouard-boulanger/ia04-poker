package sma.message.environment.notification;

import poker.card.model.Card;
import sma.message.Message;
import sma.message.MessageVisitor;
import jade.lang.acl.ACLMessage;

public class PlayerReceivedCardNotification extends Message {

	private int playerTablePositionIndex;
	private Card receivedCard;
	
	public PlayerReceivedCardNotification(){}
	public PlayerReceivedCardNotification(int playerTablePositionIndex, Card receivedCard){
		this.playerTablePositionIndex = playerTablePositionIndex;
		this.receivedCard = receivedCard;
	}

	public int getPlayerTablePositionIndex(){
		return this.playerTablePositionIndex;
	}
	
	public void setPlayerTablePositionIndex(int index){
		this.playerTablePositionIndex = index;
	}
	
	public Card getReceivedCard() {
		return receivedCard;
	}
	
	public void setReceivedCard(Card receivedCard) {
		this.receivedCard = receivedCard;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPlayerReceivedCardNotification(this, aclMsg);
	}
}
