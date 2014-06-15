package sma.message.environment.notification;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class CurrentPlayerChangedNotification extends Message {

	private AID player;
	
	public CurrentPlayerChangedNotification(){}
	
	public CurrentPlayerChangedNotification(AID currentPlayer){
		this.player = currentPlayer;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onEnvironmentChanged(this, aclMsg) | visitor.onCurrentPlayerChangedNotification(this, aclMsg);
	}

	public AID getPlayerAID() {
		return player;
	}
	
	public void setPlayerAID(AID currentPlayer){
		this.player = currentPlayer;
	}
	
}