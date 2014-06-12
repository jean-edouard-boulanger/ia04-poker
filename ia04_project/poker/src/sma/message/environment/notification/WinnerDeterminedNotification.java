package sma.message.environment.notification;

import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

import poker.game.player.model.Player;
import sma.message.Message;
import sma.message.MessageVisitor;

public class WinnerDeterminedNotification extends Message {

	ArrayList<Player> winners;
	
	public WinnerDeterminedNotification() {
		
	}
	
	public WinnerDeterminedNotification(ArrayList<Player> winners) {
		this.winners = winners;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return false;
	}

	public ArrayList<Player> getWinners() {
		return winners;
	}

	public void setWinners(ArrayList<Player> winners) {
		this.winners = winners;
	}

}
