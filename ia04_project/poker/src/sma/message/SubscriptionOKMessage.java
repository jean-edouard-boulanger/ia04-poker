package sma.message;

import poker.game.model.Game;
import jade.lang.acl.ACLMessage;

/**
 * Represents a simple message without any data, informing the receiver 
 * that the request was fulfilled.
 */
public class SubscriptionOKMessage extends OKMessage {
	
	private Game game;
	
	public SubscriptionOKMessage(){ }
	
	public SubscriptionOKMessage(Game game){ 
		this.game = game;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onOKMessage(this, aclMsg) || visitor.onSubscriptionOKMessage(this, aclMsg);
	}
	
}
