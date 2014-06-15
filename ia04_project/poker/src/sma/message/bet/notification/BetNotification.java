package sma.message.bet.notification;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class BetNotification extends Message {

	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onBetNotification(this, aclMsg);
	}

}
