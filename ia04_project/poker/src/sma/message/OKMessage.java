package sma.message;

import jade.lang.acl.ACLMessage;

/**
 * Represents a simple message without any data, informing the receiver 
 * that the request was fulfilled.
 */
public class OKMessage extends Message {
	
	public OKMessage(){ }
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onOKMessage(this, aclMsg);
	}
	
}
