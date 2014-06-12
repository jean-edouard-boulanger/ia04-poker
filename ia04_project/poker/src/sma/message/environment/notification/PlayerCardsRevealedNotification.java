package sma.message.environment.notification;

import poker.game.player.model.Player;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class PlayerCardsRevealedNotification extends Message {

	Player player;
	
	public PlayerCardsRevealedNotification() {}
	
	public PlayerCardsRevealedNotification(Player player) {
		this.player = player;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPlayerCardsRevealedNotification(this, aclMsg);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
