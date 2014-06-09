package sma.message.environment.request;

import poker.game.model.BlindValueDefinition;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class ChangeBlindValueDefinitionRequest extends Message {

	BlindValueDefinition newBlindValueDefinition;
	
	public ChangeBlindValueDefinitionRequest(){}
	
	public ChangeBlindValueDefinitionRequest(BlindValueDefinition newBlindValueDefinition) {
		this.newBlindValueDefinition = newBlindValueDefinition;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onChangeBlindValueDefinitionRequest(this, aclMsg);
	}

	public BlindValueDefinition getNewBlindValueDefinition() {
		return newBlindValueDefinition;
	}

	public void setNewBlindValueDefinition(
			BlindValueDefinition newBlindValueDefinition) {
		this.newBlindValueDefinition = newBlindValueDefinition;
	}
}
