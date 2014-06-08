package sma.message.environment.request;

import poker.card.model.Card;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class DealCardToPlayerRequest extends Message {

	private AID playerAID;
	private Card dealtCard;

	public DealCardToPlayerRequest(){}
	
	public DealCardToPlayerRequest(AID playerAid, Card dealtCard){
		this.playerAID = playerAid;
		this.dealtCard = dealtCard;
	}
	
	public AID getPlayerAID() {
		return playerAID;
	}

	public void setPlayerAID(AID playerAID) {
		this.playerAID = playerAID;
	}

	public Card getDealtCard() {
		return dealtCard;
	}

	public void setDealtCard(Card dealtCard) {
		this.dealtCard = dealtCard;
	}

	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onDealCardToPlayerRequest(this, aclMsg);
	}
}
