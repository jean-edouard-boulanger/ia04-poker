package sma.message.environment.request;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import poker.token.model.TokenSet;
import sma.message.Message;
import sma.message.MessageVisitor;

public class PlayerFoldedRequest extends Message {

	private AID playerAID;
	
	public PlayerFoldedRequest(){}
	
	public PlayerFoldedRequest(AID player) {
		this.playerAID = player;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPlayerFoldedRequest(this, aclMsg);
	}

	public AID getPlayerAID() {
		return playerAID;
	}

	public void setPlayerAID(AID player) {
		this.playerAID = player;
	}

}
