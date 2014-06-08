package sma.message.environment.notification;

import sma.message.Message;
import sma.message.MessageVisitor;
import jade.lang.acl.ACLMessage;

public class CommunityCardsEmptiedNotification extends Message {

	public CommunityCardsEmptiedNotification(){}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onCommunityCardsEmptiedNotification(this, aclMsg);
	}
}
