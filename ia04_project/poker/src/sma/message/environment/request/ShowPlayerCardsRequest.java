package sma.message.environment.request;

import poker.game.player.model.Player;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class ShowPlayerCardsRequest extends Message {

	Player player;
	
	public ShowPlayerCardsRequest() {}
	
	public ShowPlayerCardsRequest(Player player) {
		this.player = player;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onShowPlayerCardsRequest(this, aclMsg);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
