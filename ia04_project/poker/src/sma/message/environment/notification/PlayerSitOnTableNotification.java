package sma.message.environment.notification;

import poker.game.player.model.Player;
import sma.message.Message;
import sma.message.MessageVisitor;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class PlayerSitOnTableNotification extends Message {
	
	private Player newPlayer;
	
	public PlayerSitOnTableNotification(){}
	
	public PlayerSitOnTableNotification(Player newPlayer){
		this.newPlayer = newPlayer;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPlayerSitOnTableNotification(this, aclMsg);
	}

	public Player getNewPlayer() {
		return newPlayer;
	}

	public void setNewPlayer(Player newPlayer) {
		this.newPlayer = newPlayer;
	}
}
