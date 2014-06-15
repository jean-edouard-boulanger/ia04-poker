package sma.message.bet.request;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class MergeBetsRequest extends Message {

	public MergeBetsRequest(){}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onMergeBetsRequest(this, aclMsg);
	}

}
