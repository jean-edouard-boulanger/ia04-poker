package sma.message.environment.request;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import poker.token.model.TokenSet;
import sma.message.Message;
import sma.message.MessageVisitor;

public class GiveTokenSetToPlayerRequest extends Message {

	private TokenSet tokenSet;
	private AID playerAID;
	
	public GiveTokenSetToPlayerRequest() {}
	
	public GiveTokenSetToPlayerRequest(TokenSet tokenSet, AID playerAID) {
		this.tokenSet = tokenSet;
		this.playerAID = playerAID;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onGiveTokenSetToPlayerRequest(this, aclMsg);
	}

	public TokenSet getTokenSet() {
		return tokenSet;
	}

	public void setTokenSet(TokenSet tokenSet) {
		this.tokenSet = tokenSet;
	}

	public AID getPlayerAID() {
		return playerAID;
	}

	public void setPlayerAID(AID playerAID) {
		this.playerAID = playerAID;
	}

}
