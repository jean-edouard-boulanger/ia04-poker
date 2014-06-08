package sma.message.environment.request;

import poker.game.player.model.Player;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class AddPlayerTableRequest extends Message {

	Player newPlayer;
	
	public AddPlayerTableRequest(){}
	
	public AddPlayerTableRequest(Player newPlayer){
		this.newPlayer = newPlayer;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return false;
	}

	public Player getNewPlayer() {
		return newPlayer;
	}

	public void setNewPlayer(Player newPlayer) {
		this.newPlayer = newPlayer;
	}
	
}
