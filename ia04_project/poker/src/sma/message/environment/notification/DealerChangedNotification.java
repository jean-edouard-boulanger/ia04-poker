package sma.message.environment.notification;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class DealerChangedNotification extends Message {

	private AID dealer;
	
	public DealerChangedNotification(){}
	
	public DealerChangedNotification(AID dealer){
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
		return visitor.onDealerChangedNotification(this, aclMsg);
	}
}
