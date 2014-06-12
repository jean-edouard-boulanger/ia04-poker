package sma.message.determine_winner;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class DetermineWinnerRequest extends Message {
	
	public DetermineWinnerRequest(){}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onDetermineWinnerRequest(this, aclMsg);
	}
}
