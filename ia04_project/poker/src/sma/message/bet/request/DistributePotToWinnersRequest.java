package sma.message.bet.request;

import java.util.ArrayList;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;



public class DistributePotToWinnersRequest extends Message {
	
	ArrayList<AID> winnersAIDs;
	
	public DistributePotToWinnersRequest(){}
	
	public DistributePotToWinnersRequest(ArrayList<AID> winnersAIDs){
		this.winnersAIDs = winnersAIDs;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onDistributePotToWinnersRequest(this, aclMsg);
	}

	public ArrayList<AID> getWinnersAIDs() {
		return winnersAIDs;
	}

	public void setWinnersAIDs(ArrayList<AID> winnersAIDs) {
		this.winnersAIDs = winnersAIDs;
	}
	
}