package sma.message.environment.notification;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Map;

import poker.card.heuristics.combination.model.Hand;
import poker.game.player.model.WinnerPlayer;
import sma.message.Message;
import sma.message.MessageVisitor;

public class WinnerDeterminedNotification extends Message {

	ArrayList<WinnerPlayer> winners;
	
	
	
	public WinnerDeterminedNotification() {}
	
	public WinnerDeterminedNotification(ArrayList<WinnerPlayer> winners) {
		this.winners = winners;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onEnvironmentChanged(this, aclMsg) | visitor.onWinnerDeterminedNotification(this, aclMsg);
	}

	public ArrayList<WinnerPlayer> getWinners() {
		return winners;
	}

	public void setWinners(ArrayList<WinnerPlayer> winners) {
		this.winners = winners;
	}
}
