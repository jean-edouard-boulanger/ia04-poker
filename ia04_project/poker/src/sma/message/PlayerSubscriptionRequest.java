package sma.message;

import jade.lang.acl.ACLMessage;

/**
 * Represent a registration request from an AI or human player to the simulation agent
 * The request contains the name of the player.
 */
public class PlayerSubscriptionRequest extends Message {
	
	private String playerName;
	
	public PlayerSubscriptionRequest(){
		this.playerName = "unknown";
	}
	
	public PlayerSubscriptionRequest(String playerName){
		this.playerName = playerName;
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg){
		return visitor.onPlayerSubscriptionRequest(this, aclMsg);
	}
	
}
