package sma.message.environment.request;

import jade.lang.acl.ACLMessage;
import poker.card.model.Card;
import sma.message.Message;
import sma.message.MessageVisitor;

public class AddCommunityCardRequest extends Message {
	
	private Card newCard;
	
	public AddCommunityCardRequest(){}
	
	public AddCommunityCardRequest(Card newCard){
		this.newCard = newCard;
	}

	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onAddCommunityCardRequest(this, aclMsg);
	}

	public Card getNewCard() {
		return newCard;
	}

	public void setNewCard(Card newCard) {
		this.newCard = newCard;
	}
}