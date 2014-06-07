package sma.message;

import jade.lang.acl.ACLMessage;

public class CommunityCardsEmptiedNotification extends Message {

	public CommunityCardsEmptiedNotification(){}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onCommunityCardsEmptiedNotification(this, aclMsg);
	}
}
