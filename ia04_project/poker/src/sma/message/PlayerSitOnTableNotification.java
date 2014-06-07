package sma.message;

import poker.game.player.model.Player;
import jade.lang.acl.ACLMessage;

public class PlayerSitOnTableNotification extends Message {
	
	private Player newPlayer;
	private int playerTablePositionIndex;
	
	public PlayerSitOnTableNotification(){}
	
	public PlayerSitOnTableNotification(Player newPlayer, int playerTablepositionIndex){
		this.newPlayer = newPlayer;
		this.playerTablePositionIndex = playerTablepositionIndex;
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

	public int getPlayerTablePositionIndex() {
		return playerTablePositionIndex;
	}

	public void setPlayerTablePositionIndex(int playerTablePositionIndex) {
		this.playerTablePositionIndex = playerTablePositionIndex;
	}
	
}
