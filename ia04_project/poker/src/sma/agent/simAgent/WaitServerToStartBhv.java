package sma.agent.simAgent;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import sma.agent.helper.AgentHelper;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.PlayerSubscriptionRequest;


/**
 * This behavior wait for the server to be configured and start.
 * If any registration request is received, we send back a failure message
 * indicating the server is not ready.
 */
public class WaitServerToStartBhv extends Behaviour {
	
	private SimAgent simAgent;
	
	public WaitServerToStartBhv(SimAgent agent){
		super(agent);
		this.simAgent = agent;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		System.out.println(simAgent.getLocalName() + " now waiting server to start ...");
	};
	
	@Override	
	public void action() {
		
		boolean msgReceived = AgentHelper.receiveMessage(this.myAgent, ACLMessage.SUBSCRIBE, new MessageVisitor(){
			@Override
			public boolean onPlayerSubscriptionRequest(PlayerSubscriptionRequest request, ACLMessage aclMsg){
				// subscription are not allowed at this point, we send a failure message.
				AgentHelper.sendReply(myAgent, aclMsg, ACLMessage.FAILURE, new FailureMessage("Server not ready."));
				return true;
			}
		});
		
		//if(!msgReceived)
		//	block(); 
	}
	@Override
	public boolean done() {
		return simAgent.isServerStarted();
	}
}

