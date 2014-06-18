package sma.message.blind.request;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class RefreshBlindValueDefinitionRequest extends Message {
	public RefreshBlindValueDefinitionRequest() {
		
	}

	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onRefreshBlindValueDefinitionRequest(this, aclMsg);
	}
}
