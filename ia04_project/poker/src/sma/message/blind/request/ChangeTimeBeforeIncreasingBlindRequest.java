package sma.message.blind.request;

import poker.token.model.TokenValueDefinition;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class ChangeTimeBeforeIncreasingBlindRequest extends Message {
	
	private int time;
	private TokenValueDefinition tokenValueDefinition;
	
	public ChangeTimeBeforeIncreasingBlindRequest() {
		
	}
	
	public ChangeTimeBeforeIncreasingBlindRequest(int time, TokenValueDefinition tokenValueDefinition) {
		this.time = time;
		this.tokenValueDefinition = tokenValueDefinition;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onChangeTimeBeforeIncreasingBlindRequest(this, aclMsg);
	}

	public TokenValueDefinition getTokenValueDefinition() {
		return tokenValueDefinition;
	}

	public void setTokenValueDefinition(TokenValueDefinition tokenValueDefinition) {
		this.tokenValueDefinition = tokenValueDefinition;
	}
}
