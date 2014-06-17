package sma.message;

import jade.lang.acl.ACLMessage;

public class BooleanMessage extends Message {

	boolean value;
	
	public BooleanMessage(){}
	
	public BooleanMessage(boolean value){
		this.value = value;
	}
	
	public boolean getValue(){
		return this.value;
	}
	
	public void setValue(boolean value){
		this.value = value;
	}

	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onBooleanMessage(this, aclMsg);
	}
	
}
