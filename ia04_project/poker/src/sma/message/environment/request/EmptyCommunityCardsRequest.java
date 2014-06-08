package sma.message.environment.request;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class EmptyCommunityCardsRequest extends Message {

	public EmptyCommunityCardsRequest() {}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onEmptyCommunityCardsRequest(this, aclMsg);
	}
}
