package sma.message.bet.request;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class GetPotAmountRequest extends Message {

	public GetPotAmountRequest(){}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onGetPotAmountRequest(this, aclMsg);
	}
}
