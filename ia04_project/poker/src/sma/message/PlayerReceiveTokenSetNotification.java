package sma.message;

import poker.token.model.TokenSet;
import jade.lang.acl.ACLMessage;

public class PlayerReceiveTokenSetNotification extends Message {
	
	private int playerTablePositionIndex;
	private TokenSet receivedTokenSet;
	
	public PlayerReceiveTokenSetNotification(){}
	
	public PlayerReceiveTokenSetNotification(int playerTablePositionIndex, TokenSet receivedTokenSet){
		this.playerTablePositionIndex = playerTablePositionIndex;
		this.receivedTokenSet = receivedTokenSet;
	}
	
	public int getPlayerTablePositionIndex() {
		return playerTablePositionIndex;
	}

	public void setPlayerTablePositionIndex(int playerTablePositionIndex) {
		this.playerTablePositionIndex = playerTablePositionIndex;
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