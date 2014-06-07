package sma.message;

import poker.card.model.Card;
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
		return visitor.onCardAddedToCommunityCardsNotification(this, aclMsg);
	}	
}
