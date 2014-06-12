package sma.message.environment.request;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class SetDealerRequest extends Message {

	private AID dealer;
	
	public SetDealerRequest(){}
	
	public SetDealerRequest(AID dealer){
		this.setDealer(dealer);
	}

	public AID getDealer() {
		return dealer;
	}

	public void setDealer(AID dealer) {
		this.dealer = dealer;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onSetDealerRequest(this, aclMsg);
	}
}
