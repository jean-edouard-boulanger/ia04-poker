package sma.message.environment.notification;

import poker.token.model.TokenSet;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class TokenSetSentFromPotToPlayerNotification extends Message {

	private TokenSet sentTokenSet;
	private AID playerAID;
	
	public TokenSetSentFromPotToPlayerNotification(){}
	
	public TokenSetSentFromPotToPlayerNotification(AID playerAID, TokenSet sentTokenSet){
		this.playerAID = playerAID;
		this.sentTokenSet = sentTokenSet;
	}

	public TokenSet getSentTokenSet() {
		return sentTokenSet;
	}

	public void setSentTokenSet(TokenSet sentTokenSet) {
		this.sentTokenSet = sentTokenSet;
	}

	public AID getPlayerAID() {
		return playerAID;
	}

	public void setPlayerAID(AID playerAID) {
		this.playerAID = playerAID;
	}

	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onEnvironmentChanged(this, aclMsg) | visitor.onTokenSetSentFromPotToPlayerNotification(this, aclMsg);
	}
}