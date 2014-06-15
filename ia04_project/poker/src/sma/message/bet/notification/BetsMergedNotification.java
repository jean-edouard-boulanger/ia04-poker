package sma.message.bet.notification;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class BetsMergedNotification extends Message {

	public BetsMergedNotification(){}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onBetsMergedNotification(this, aclMsg);
	}

}
