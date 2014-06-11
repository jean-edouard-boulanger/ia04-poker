package sma.message.environment.notification;

import poker.card.model.Card;
import sma.message.Message;
import sma.message.MessageVisitor;
import jade.lang.acl.ACLMessage;

public class CardAddedToCommunityCardsNotification extends Message {
	
	Card newCommunityCard;
	
	public CardAddedToCommunityCardsNotification(){}
	
	public CardAddedToCommunityCardsNotification(Card newCommunityCard){
		this.newCommunityCard = newCommunityCard;
	}
	
	public Card getNewCommunityCard(){
		return this.newCommunityCard;
	}
	
	public void setNewCommunityCard(Card newCommunityCard){
		this.newCommunityCard = newCommunityCard;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onEnvironmentChanged(this, aclMsg) | visitor.onCardAddedToCommunityCardsNotification(this, aclMsg);
	}	
}
