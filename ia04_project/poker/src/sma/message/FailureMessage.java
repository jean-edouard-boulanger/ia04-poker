package sma.message;

import jade.lang.acl.ACLMessage;

public class FailureMessage extends Message {
	
	private String message;
	
	public FailureMessage(String message){
		this.message = message;  
	}
	
	public FailureMessage(){
		this.message = "";  
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onFailureMessage(this, aclMsg);
	}
	
}
