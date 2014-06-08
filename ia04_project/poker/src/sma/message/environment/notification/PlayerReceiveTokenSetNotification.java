package sma.message.environment.notification;

import poker.token.model.TokenSet;
import sma.message.Message;
import sma.message.MessageVisitor;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class PlayerReceiveTokenSetNotification extends Message {
	
	private AID playerAID;
	private TokenSet receivedTokenSet;
	
	public PlayerReceiveTokenSetNotification(){}
	
	public PlayerReceiveTokenSetNotification(int playerTablePositionIndex, AID playerAID){
		this.playerAID = playerAID;
		this.receivedTokenSet = receivedTokenSet;
	}
	
	public AID getPlayerAID() {
		return playerAID;
	}

	public void setPlayerAID(AID playerAID) {
		this.playerAID = playerAID;
	}

	public TokenSet getReceivedTokenSet() {
		return receivedTokenSet;
	}

	public void setReceivedTokenSet(TokenSet receivedTokenSet) {
		this.receivedTokenSet = receivedTokenSet;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPlayerReceiveTokenSetNotification(this, aclMsg);
	}
}