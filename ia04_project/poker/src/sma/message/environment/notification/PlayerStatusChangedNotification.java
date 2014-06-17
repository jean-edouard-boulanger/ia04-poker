package sma.message.environment.notification;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import poker.game.player.model.PlayerStatus;
import sma.message.Message;
import sma.message.MessageVisitor;

public class PlayerStatusChangedNotification extends Message {

	private AID playerAID;
	private PlayerStatus newStatus;
	
	public PlayerStatusChangedNotification(AID playerAid, PlayerStatus newStatus){
		this.playerAID = playerAid;
		this.newStatus = newStatus;
	}
	
	public PlayerStatusChangedNotification(){}
	
	public void setPlayerAID(AID playerAID){
		this.playerAID = playerAID;
	}
	
	public AID getPlayerAID(){
		return this.playerAID;
	}
	
	public void setNewStatus(PlayerStatus newStatus){
		this.newStatus = newStatus;
	}
	
	public PlayerStatus getNewStatus(){
		return this.newStatus;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPlayerStatusChangedNotification(this, aclMsg);
	}
}
