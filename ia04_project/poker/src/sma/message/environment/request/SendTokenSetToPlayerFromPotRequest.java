package sma.message.environment.request;

import poker.token.model.TokenSet;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class SendTokenSetToPlayerFromPotRequest extends Message {
	
	private TokenSet sentTokenSet;
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		// TODO Auto-generated method stub
		return false;
	}
}
