package sma.message.environment.notification;

import poker.token.model.TokenSet;
import sma.message.Message;
import sma.message.MessageVisitor;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class BetNotification extends Message {

	TokenSet betTokenSet;
	private AID playerAID;
	int betAmount;
	
	public int getBetAmount() {
		return betAmount;
	}

	public void setBetAmount(int betAmount) {
		this.betAmount = betAmount;
	}

	public BetNotification(){}
	
	public BetNotification(AID playerAID, TokenSet betTokenSet, int betAmount){
		this.playerAID = playerAID;
		this.betTokenSet = betTokenSet;
		this.betAmount = betAmount;
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
		return visitor.onEnvironmentChanged(this, aclMsg) | visitor.onBetNotification(this, aclMsg);
	}
}
