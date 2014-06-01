package sma.agent.helper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Class providing various utility methods for Jade agents.
 *
 */
public class AgentHelper {
	
	/**
	 * Register a service to the default DFservice
	 * @param name	name of the service to register
	 * @param type	type of the service to register
	 * For now, behavior is undefined if this function is called twice.
	 */
	public static void registerService(Agent agent, String name, String type){
		
		//TODO: allows the creation of severals service by successive call of this function
		// 		we have to check in there is already a DFAgentDescription in the DFService
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(agent.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(name);
		sd.setType(type);
		dfd.addServices(sd);
		try {
			DFService.register(agent, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	/**
	 * Check if a message matching the template is available in the given agent FIFO.
	 * The message object is the handled by the given visitor object.
	 * If the message was not processed by the visitor (accept() returned false) then 
	 * the message is put back in the agent FIFO.
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
		if(!msg.accept(visitor, ACLmsg)){
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
			reply.setContent(data.toJson());
			agent.send(reply);
		} catch (IOException e) {
			System.out.println("[" + agent.getName() + "] Error while serializing reply, the message was not sent (" + e.getMessage() + ").");
		}
	}
}
