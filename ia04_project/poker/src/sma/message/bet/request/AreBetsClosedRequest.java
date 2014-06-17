package sma.message.bet.request;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class AreBetsClosedRequest extends Message {

	public AreBetsClosedRequest(){}

	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onAreBetsClosedRequest(this, aclMsg);
	}
	
}
