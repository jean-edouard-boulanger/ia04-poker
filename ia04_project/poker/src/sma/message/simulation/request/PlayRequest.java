package sma.message.simulation.request;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class PlayRequest extends Message {

	boolean requestResentFollowedToError = false;
	String errorMessage;
	
	public PlayRequest() {}
	
	public PlayRequest(boolean requestResentFollowedToError, String errorMessage){
		this.requestResentFollowedToError = requestResentFollowedToError;
		this.errorMessage = errorMessage;
	}

	public boolean isRequestResentFollowedToError() {
		return requestResentFollowedToError;
	}

	public void setRequestResentFollowedToError(boolean requestResentFollowedToError) {
		this.requestResentFollowedToError = requestResentFollowedToError;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onPlayRequest(this, aclMsg);
	}
}
