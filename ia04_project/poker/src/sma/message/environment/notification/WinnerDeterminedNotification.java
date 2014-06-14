package sma.message.environment.notification;

import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Map;

import poker.card.heuristics.combination.model.Hand;
import poker.game.player.model.Player;
import sma.message.Message;
import sma.message.MessageVisitor;

public class WinnerDeterminedNotification extends Message {

	Map<Player, Hand> winners;
	
	public WinnerDeterminedNotification() {
		
	}
	
	public WinnerDeterminedNotification(Map<Player, Hand> winners) {
		this.winners = winners;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return false;
	}

	public Map<Player, Hand> getWinners() {
		return winners;
	}

	public void setWinners(Map<Player, Hand> winners) {
		this.winners = winners;
	}

}
