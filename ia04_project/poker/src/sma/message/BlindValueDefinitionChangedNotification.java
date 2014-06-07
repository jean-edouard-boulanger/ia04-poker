package sma.message;

import poker.game.model.BlindValueDefinition;
import jade.lang.acl.ACLMessage;

public class BlindValueDefinitionChangedNotification extends Message {
	
	public BlindValueDefinition newBlindValueDefinition;
	
	public BlindValueDefinitionChangedNotification(){}
	
	public BlindValueDefinitionChangedNotification(BlindValueDefinition newBlindValueDefinition){
		this.newBlindValueDefinition = newBlindValueDefinition;
	}

	public BlindValueDefinition getNewBlindValueDefinition() {
		return newBlindValueDefinition;
	}

	public void setNewBlindValueDefinition(BlindValueDefinition newBlindValueDefinition) {
		this.newBlindValueDefinition = newBlindValueDefinition;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onBlindValueDefinitionChangedNotification(this, aclMsg);
	}
}
