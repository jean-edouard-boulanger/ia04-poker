package sma.message.environment.notification;

import jade.lang.acl.ACLMessage;
import poker.game.model.BlindValueDefinition;
import poker.token.model.TokenValueDefinition;
import sma.message.Message;
import sma.message.MessageVisitor;

public class TokenValueDefinitionChangedNotification extends Message {

	private TokenValueDefinition tokenValueDefinition;
	
	public TokenValueDefinitionChangedNotification(){}
	
	public TokenValueDefinitionChangedNotification(TokenValueDefinition tokenValueDefinition){
		this.tokenValueDefinition = tokenValueDefinition;
	}
	
	public TokenValueDefinition getTokenValueDefinition() {
		return tokenValueDefinition;
	}

	public void setTokenValueDefinition(TokenValueDefinition tokenValueDefinition) {
		this.tokenValueDefinition = tokenValueDefinition;
	}

	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onEnvironmentChanged(this, aclMsg) | visitor.onTokenValueDefinitionChangedNotification(this, aclMsg);
	}
}
