package sma.message.dealer.request;

import poker.game.model.Round;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class DealRequest extends Message{

	Round handStep;
	
	public DealRequest(){}
	
	public DealRequest(Round handStep){this.handStep = handStep;}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onDealRequest(this, aclMsg);
	}	
	
	public void setHandStep(Round handStep){
		this.handStep = handStep;
	}
	
	public Round getHandStep(){
		return this.handStep;
	}
	
}
