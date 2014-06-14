package sma.message.environment.request;

import jade.lang.acl.ACLMessage;
import poker.game.model.BlindValueDefinition;
import poker.token.model.TokenValueDefinition;
import sma.message.Message;
import sma.message.MessageVisitor;

public class SetTokenValueDefinitionRequest extends Message {

	private TokenValueDefinition tokenValueDefinition;
	
	public SetTokenValueDefinitionRequest(){}
	
	public SetTokenValueDefinitionRequest(TokenValueDefinition tokenValueDefinition){
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
		return visitor.onSetTokenValueDefinitionRequest(this, aclMsg);
	}
}
