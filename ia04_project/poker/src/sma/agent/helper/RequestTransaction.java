package sma.agent.helper;

import java.io.IOException;

import sma.message.Message;
import sma.message.MessageVisitor;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Represents a REQUEST-INFORM transaction (with an associated conversation-id) 
 *
 */
public class RequestTransaction {

	private final String conversation_id;
	private ACLMessage request;
	private Agent agent;
	private Behaviour bhv;
	/**
	 * Create a new transaction associated with the given behavior.
	 * @param agent	The agent whose sending the request.
	 * @param data	The data to be associated to the request.
	 * @param receiver	The AID of the receiver of the request.
	 */
	public RequestTransaction(Behaviour bhv, Message data, AID receiver){
		this(bhv,data,receiver, ACLMessage.REQUEST);
	}

	/**
	 * Create a new transaction with a custom performative.
	 * @param agent	The agent whose sending the request.
	 * @param data	The data to be associated to the request.
	 * @param receiver	The AID of the receiver of the request.
	 * @param performative The performative of the request.
	 */
	public RequestTransaction(Behaviour bhv, Message data, AID receiver, int performative){
		this.agent = bhv.getAgent();
		this.bhv = bhv;
		this.request = new ACLMessage(performative);
		this.request.addReceiver(receiver);
		try {
			if(data != null)
				this.request.setContent(data.toJson());
		} catch (IOException e) {
			this.request.setContent("");
			e.printStackTrace();
		}
		this.conversation_id = generateConversationID();
		this.request.setConversationId(this.conversation_id);
	}

	/**
	 * Se the message data associated with the request.
	 * @param data	The data to be associated to the request.
	 * If the data could not be serialized in JSON, the message content is not send,
	 * and a error message is printed.
	 */
	public void setRequestContent(Message data){
		try {
			this.request.setContent(data.toJson());
		} catch (IOException e) {
			System.out.println("[" + agent.getLocalName() + "] Error while serializing request data, the message was not sent (" + e.getMessage() + ").");
		}
	}

	/**
	 * Send the request.
	 */
	public void sendRequest(){
		this.agent.send(this.request);
	}

	/**
	 * Check if a response a available and then process the reply using the given visitor.
	 * If the visitor is null, the matching message is discarded and the function return true.
	 * @param visitor	Visitor used to process the incoming reply.
	 * @return true if a reply was processed, false otherwise.
	 */
	public boolean checkReply(MessageVisitor visitor){
		MessageTemplate mt = MessageTemplate.MatchConversationId(this.conversation_id);

		ACLMessage ACLmsg = agent.receive(mt);

		if(ACLmsg == null)
			return false;

		Message msg;
		try {
			msg = Message.fromJson(ACLmsg.getContent());
		} catch (IOException e) {
			//TODO: handle json parsing errors.
			e.printStackTrace();
			return false;
		}

		if(visitor != null){
			if(!msg.accept(visitor, ACLmsg)) // transaction messages are discarded even if the visitor does not handle it.
				System.out.println("[" + agent.getLocalName() + "] Warning response to request not handled by visitor, the message was discarded (" + ACLmsg.getContent() + ").");
		}
		return true;
	}

	/**
	 * Generate a random conversation ID based on the agent name and behavior name.
	 * Bhv and agent member should not be null.
	 * @return a random conversation ID. 
	 */
	private String generateConversationID(){
		return this.agent.getLocalName() + "." + this.bhv.getBehaviourName() + bhv.hashCode() + System.currentTimeMillis()%10000 ;
	}



}
