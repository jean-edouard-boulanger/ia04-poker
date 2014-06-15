package sma.message.bet.notification;

import java.util.HashMap;

import poker.token.model.TokenSet;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class PotAmountNotification extends Message {

	private TokenSet pot;
	
	public PotAmountNotification(){}
	
	public PotAmountNotification(TokenSet pot){
		this.pot = pot;
	}

	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPotAmountNotification(this, aclMsg);
	}

	public TokenSet getPot() {
		return pot;
	}

	public void setPot(TokenSet pot) {
		this.pot = pot;
	}
}
