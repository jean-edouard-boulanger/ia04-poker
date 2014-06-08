package sma.message.environment.notification;

import poker.card.model.Card;
import sma.message.Message;
import sma.message.MessageVisitor;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class PlayerReceivedCardNotification extends Message {

	private AID playerAID;
	private Card receivedCard;
	
	public PlayerReceivedCardNotification(){}
	public PlayerReceivedCardNotification(AID playerAID, Card receivedCard){
		this.playerAID = playerAID;
		this.receivedCard = receivedCard;
	}

	public AID getPlayerAID() {
		return playerAID;
	}

	public void setPlayerAID(AID playerAID) {
		this.playerAID = playerAID;
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
