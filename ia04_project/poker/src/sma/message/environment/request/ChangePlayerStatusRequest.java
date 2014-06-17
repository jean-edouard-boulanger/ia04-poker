package sma.message.environment.request;

import poker.game.player.model.PlayerStatus;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class ChangePlayerStatusRequest extends Message {

	private AID playerAID;
	private PlayerStatus newStatus;
	
	public ChangePlayerStatusRequest(AID playerAid, PlayerStatus newStatus){
		this.playerAID = playerAid;
		this.newStatus = newStatus;
	}
	
	public ChangePlayerStatusRequest(){}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onChangePlayerStatusRequest(this, aclMsg);
	}

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
}
