package sma.message.dealer.request;

import poker.game.model.HandStep;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class DealRequest extends Message{

	HandStep handStep;
	
	public DealRequest(){}
	
	public DealRequest(HandStep handStep){this.handStep = handStep;}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onDealRequest(this, aclMsg);
	}	
	
	public void setHandStep(HandStep handStep){
		this.handStep = handStep;
	}
	
	public HandStep getHandStep(){
		return this.handStep;
	}
	
}
