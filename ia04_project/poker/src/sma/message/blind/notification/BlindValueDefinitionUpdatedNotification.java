package sma.message.blind.notification;

import poker.game.model.BlindValueDefinition;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class BlindValueDefinitionUpdatedNotification extends Message {

	BlindValueDefinition blindValueDefinition;
	
	public BlindValueDefinitionUpdatedNotification() {
		
	}
	
	public BlindValueDefinitionUpdatedNotification(BlindValueDefinition blindValueDefinition) {
		this.blindValueDefinition = blindValueDefinition;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onBlindValueDefinitionUpdatedNotification(this, aclMsg);
	}

	public BlindValueDefinition getBlindValueDefinition() {
		return blindValueDefinition;
	}

	public void setBlindValueDefinition(BlindValueDefinition blindValueDefinition) {
		this.blindValueDefinition = blindValueDefinition;
	}
}
