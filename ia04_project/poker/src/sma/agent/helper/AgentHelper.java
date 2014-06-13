package sma.agent.helper;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Class providing various utility methods for Jade agents.
 *
 */
public class AgentHelper {
	
	/**
	 * Check if a message matching the template is available in the given agent FIFO.
	 * The message object is the handled by the given visitor object.
	 * If the message was not processed by the visitor (accept() returned false) then 
	 * the message is put back in the agent FIFO.
	 * If the visitor is null the matching message is discarded.
	 * @param agent		Agent whose FIFO will be inspected.
	 * @param template	Template used for message filtering.
	 * @param visitor	Visitor used to handle the message
	 * @return true if a message was processed, false otherwise.
	 */
	public static boolean receiveMessage(Agent agent, MessageTemplate template, MessageVisitor visitor){
		ACLMessage ACLmsg = agent.receive(template);
		
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

		if(visitor == null){
			System.err.println("SEVERE WARNING [" + agent.getLocalName() + " : AgentHelper.receiveMessage] Message visitor passed to receiveMessage is null");
			agent.putBack(ACLmsg);
			return false;
		}
		
		// Horseshit !
		if(!msg.accept(visitor, ACLmsg)){
			System.out.println("[" + agent.getLocalName() + "] Warning non-handled message (" + ACLmsg.getContent() + ").");
			agent.putBack(ACLmsg);
			return false;
		}
		return true;
	}
	
	/**
	 * Check if a message matching the given peroformative is available in the given agent FIFO.
	 * The message object is the handled by the given visitor object.
	 * If the message was not processed by the visitor (accept() returned false) then 
	 * the message is put back in the agent FIFO.
	 * @param agent		Agent whose FIFO will be inspected.
	 * @param performative	Performative used for message filtering.
	 * @param visitor	Visitor used to handle the message
	 * @return true if a message was processed, false otherwise.
	 */
	public static boolean receiveMessage(Agent agent, int performative, MessageVisitor visitor){
		return receiveMessage(agent, MessageTemplate.MatchPerformative(performative), visitor);
	}
	
	/**
	 * Send a reply corresponding to the given message.
	 * @param agent Agent whose is sending the reply.
	 * @param msg	Message to reply to.
	 * @param performative	The performative of the reply.
	 * @param data	Message Data set as reply content.
	 * 
	 * If the message data can not be serialized in JSON, an error message is displayed and the message is not
	 * sent.
	 */
	public static void sendReply(Agent agent, ACLMessage msg,int performative, Message data){
		try {
			ACLMessage reply = msg.createReply();
			reply.setPerformative(performative);
			if(data != null)
				reply.setContent(data.toJson());
			agent.send(reply);
		} catch (IOException e) {
			System.out.println("[" + agent.getLocalName() + "] Error while serializing reply, the message was not sent (" + e.getMessage() + ").");
		}
	}
	
	public static void sendSimpleMessage(Agent sender, AID receiver, int performative, Message content){
		ArrayList<AID> receivers = new ArrayList<AID>();
		receivers.add(receiver);
		
		sendSimpleMessage(sender, receivers, performative, content);
	}
	
	public static void sendSimpleMessage(Agent sender, ArrayList<AID> receivers, int performative, Message content){
		try{
			ACLMessage msg = new ACLMessage(performative);
			for(AID receiver : receivers) {
				msg.addReceiver(receiver);
			}
			if(content != null)
				msg.setContent(content.toJson());
			msg.setSender(sender.getAID());
			
			sender.send(msg);
		} catch(IOException ex){
			System.out.println("[" + sender.getLocalName() + "] Error while serializing mssa, the message was not sent (" + ex.getMessage() + ").");
		}
	}	
}
