package sma.message.environment.notification;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class PotEmptiedNotification extends Message {

	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPotEmptiedNotification(this, aclMsg);
	}

}
