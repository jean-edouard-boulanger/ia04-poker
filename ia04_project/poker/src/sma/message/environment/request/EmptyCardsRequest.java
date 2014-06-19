package sma.message.environment.request;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class EmptyCardsRequest extends Message {

	public EmptyCardsRequest() {}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onEmptyCardsRequest(this, aclMsg);
	}
}
