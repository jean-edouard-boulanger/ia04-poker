package sma.message.determine_winner;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.Map;

import poker.card.heuristics.combination.model.Hand;
import sma.message.Message;
import sma.message.MessageVisitor;

public class WinnerDeterminedResponse extends Message {

	Map<AID, Hand> winners;
	
	public WinnerDeterminedResponse() {}
	
	public WinnerDeterminedResponse(Map<AID, Hand> winners) {
		this.winners = winners;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onWinnerDeterminedResponse(this, aclMsg);
	}

	public Map<AID, Hand> getWinners() {
		return winners;
	}

	public void setWinners(Map<AID, Hand> winners) {
		this.winners = winners;
	}
}
