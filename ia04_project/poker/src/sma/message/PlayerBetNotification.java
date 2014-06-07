package sma.message;

import poker.token.model.TokenSet;
import jade.lang.acl.ACLMessage;

public class PlayerBetNotification extends Message {

	TokenSet betTokenSet;
	private int playerTablePositionIndex;
	
	public PlayerBetNotification(){}
	
	public PlayerBetNotification(int playerTablePositionIndex, TokenSet betTokenSet){
		this.playerTablePositionIndex = playerTablePositionIndex;
		this.betTokenSet = betTokenSet;
	}

	public TokenSet getBetTokenSet() {
		return betTokenSet;
	}

	public void setBetTokenSet(TokenSet betTokenSet) {
		this.betTokenSet = betTokenSet;
	}

	public int getPlayerTablePositionIndex() {
		return playerTablePositionIndex;
	}

	public void setPlayerTablePositionIndex(int playerTablePositionIndex) {
		this.playerTablePositionIndex = playerTablePositionIndex;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPlayerBetNotification(this, aclMsg);
	}
}
