package sma.agent.helper;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.OKMessage;

/**
 * Simple message visitor that display a custom message on FailureMessag and OkMessage;
 */
public class SimpleVisitor extends MessageVisitor {

    private String okMessage;
    private String failMessage;
    private String receiverName;

    public SimpleVisitor(Agent receiver, String okMessage, String failMessage){
	super();
	this.receiverName = receiver.getLocalName();
	this.okMessage = okMessage;
	this.failMessage = failMessage;
    }

    public SimpleVisitor(Agent receiver, String failMessage){
	this(receiver, null, failMessage);
    }

    @Override
    public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
	if(this.okMessage != null)
	    System.out.println("[" + receiverName + "] " + this.okMessage + " [message from: " + aclMsg.getSender().getLocalName() + "]");
	return true;
    }

    @Override
    public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
	if(this.okMessage != null)
	    System.out.println("[" + receiverName + "] " + this.failMessage + " [error: " + msg.getMessage() + ", message from: " 
		    + aclMsg.getSender().getLocalName() + "]");
	return true;
    }

}
