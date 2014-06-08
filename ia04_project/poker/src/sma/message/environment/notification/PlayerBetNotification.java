package sma.message.environment.notification;

import poker.token.model.TokenSet;
import sma.message.Message;
import sma.message.MessageVisitor;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class PlayerBetNotification extends Message {

	TokenSet betTokenSet;
	private AID playerAID;

	
	public PlayerBetNotification(){}
	
	public PlayerBetNotification(AID playerAID, TokenSet betTokenSet){
		this.playerAID = playerAID;
		this.betTokenSet = betTokenSet;
	}

	public TokenSet getBetTokenSet() {
		return betTokenSet;
	}

	public void setBetTokenSet(TokenSet betTokenSet) {
		this.betTokenSet = betTokenSet;
	}

	public AID getPlayerAID() {
		return playerAID;
	}

	public void setPlayerAID(AID playerAID) {
		this.playerAID = playerAID;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPlayerBetNotification(this, aclMsg);
	}
}
