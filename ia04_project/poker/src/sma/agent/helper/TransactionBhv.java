package sma.agent.helper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import poker.game.player.model.Player;
import sma.agent.SimulationAgent;
import sma.agent.helper.AgentHelper;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.PlayerSubscriptionRequest;

/**
 * This behavior simply send a request and wait for the answer.
 * A MessageVisitor object can be set inOrder to handle the request answer.
 * The default visitor print an error message on failure.
 */
public class TransactionBhv extends Behaviour
{
	private RequestTransaction transaction;
	private MessageVisitor visitor;
	private boolean requestHandled;

	public TransactionBhv(Agent agent, Message requestMessage, AID requestReceiver, int performative){
		super(agent);
		this.requestHandled = false;
		this.transaction = new RequestTransaction(this, requestMessage, requestReceiver, performative);
		this.visitor = new SimpleVisitor(agent, "unhandled transaction failure");
	}

	public TransactionBhv(Agent agent, Message requestMessage, AID requestReceiver){
		this(agent,requestMessage, requestReceiver, ACLMessage.REQUEST);
	}

	public void setResponseVisitor(MessageVisitor visitor){
		this.visitor = visitor;
	}

	@Override
	public void onStart(){
		transaction.sendRequest();
	}

	/**
	 * Register players if there is enough room (only if the server is started and the game not running).
	 */
	@Override
	public void action() {

		if(!transaction.checkReply(this.visitor))
			block();
		else
			this.requestHandled = true;

	}

	@Override
	public boolean done() {
		return requestHandled;
	}

}