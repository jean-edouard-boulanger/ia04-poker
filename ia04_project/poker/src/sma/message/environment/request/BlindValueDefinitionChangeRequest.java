package sma.message.environment.request;

import jade.lang.acl.ACLMessage;
import poker.game.model.BlindValueDefinition;
import sma.message.Message;
import sma.message.MessageVisitor;

public class BlindValueDefinitionChangeRequest extends Message {

	private BlindValueDefinition blindValueDefinition;
	
	public BlindValueDefinitionChangeRequest(){}
	
	public BlindValueDefinitionChangeRequest(BlindValueDefinition blindValueDefinition){
		this.blindValueDefinition = blindValueDefinition;
	}

	public BlindValueDefinition getBlindValueDefinition() {
		return blindValueDefinition;
	}

	public void setBlindValueDefinition(BlindValueDefinition blindValueDefinition) {
		this.blindValueDefinition = blindValueDefinition;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return false;
	}
}
