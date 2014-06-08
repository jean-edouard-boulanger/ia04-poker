package sma.message.environment.notification;

import poker.game.player.model.Player;
import sma.message.Message;
import sma.message.MessageVisitor;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class PlayerSitOnTableNotification extends Message {
	
	private Player newPlayer;
	private AID playerAID;
	
	public PlayerSitOnTableNotification(){}
	
	public PlayerSitOnTableNotification(Player newPlayer, AID playerAID){
		this.newPlayer = newPlayer;
		this.playerAID = playerAID;
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

	public AID getPlayerAID() {
		return playerAID;
	}

	public void setPlayerAID(AID playerAID) {
		this.playerAID = playerAID;
	}
}
