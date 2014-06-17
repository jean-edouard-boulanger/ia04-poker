package sma.message.bet.request;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class FoldRequest extends Message {
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onFoldRequest(this, aclMsg);
	}	
}
