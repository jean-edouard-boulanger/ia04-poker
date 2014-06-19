package sma.message.environment.notification;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.Map;

import poker.card.heuristics.combination.model.Hand;
import sma.message.Message;
import sma.message.MessageVisitor;

public class WinnerDeterminedNotification extends Message {

	Map<AID, Hand> winners;
	
	public WinnerDeterminedNotification() {}
	
	public WinnerDeterminedNotification(Map<AID, Hand> winners) {
		this.winners = winners;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onEnvironmentChanged(this, aclMsg) | visitor.onWinnerDeterminedNotification(this, aclMsg);
	}

	public Map<AID, Hand> getWinners() {
		return winners;
	}

	public void setWinners(Map<AID, Hand> winners) {
		this.winners = winners;
	}
}
