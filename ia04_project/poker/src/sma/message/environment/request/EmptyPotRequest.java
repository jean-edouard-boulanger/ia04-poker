package sma.message.environment.request;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class EmptyPotRequest extends Message {

	public EmptyPotRequest() {}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onEmptyPotRequest(this, aclMsg);
	}
}
