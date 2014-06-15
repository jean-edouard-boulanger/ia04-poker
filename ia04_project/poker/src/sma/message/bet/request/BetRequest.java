package sma.message.bet.request;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class BetRequest extends Message {

	public BetRequest(){}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onBetRequest(this, aclMsg);
	}

}
