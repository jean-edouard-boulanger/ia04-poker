package sma.agent;

import gui.server.ServerWindow;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.beans.PropertyChangeSupport;
import java.io.IOException;

import poker.game.model.Game;
import sma.agent.helper.AgentHelper;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.PlayerSubscriptionRequest;

/**
 * Simulation agent.
 * This agent handle the main steps of the poker simulation as well as game configuration.
 *
 */
public class SimAgent extends GuiAgent {
	
	private Game game;
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	private boolean serverStarted = false;
	private boolean gameStarted = false;
	
	public enum ServerGuiEvent {LAUNCH_SERVER, LAUNCH_GAME};
	

	
	
	public void setup()
	{
		super.setup();
		AgentHelper.registerService(this, "PokerSimulation","Simulation");
		
		ServerWindow server_window = new ServerWindow(this);
		changes.addPropertyChangeListener(server_window);
		
		addBehaviour(new waitServerToStartBhv());
	}
	
	/**
	 * This Behaviour wait for the server to be configured and start.
	 * If any registration request is received, we send back a failure message
	 * indicating the server is not ready.
	 */
	public class waitServerToStartBhv extends Behaviour {
		@Override	
		public void action() {
			
			boolean msgReceived = AgentHelper.receiveMessage(this.myAgent, ACLMessage.SUBSCRIBE, new MessageVisitor(){
				@Override
				public boolean onPlayerSubscriptionRequest(PlayerSubscriptionRequest request, ACLMessage aclMsg){
					AgentHelper.sendReply(myAgent, aclMsg, ACLMessage.FAILURE, new FailureMessage("Server not ready."));
					return true;
				}
			});
			
			if(!msgReceived)
				block();
		}
		@Override
		public boolean done() {
			return serverStarted;
		}
	}
	/**
	 * This behavior wait player subscriptions.
	 * The behavior stop when the user click 'Start the game' in the server gui.
	 */
	public class waitPlayersBhv extends Behaviour
	{
		private boolean done = false;
		
		@Override
		public void action() {
			
			boolean msgReceived = AgentHelper.receiveMessage(this.myAgent, MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE), new MessageVisitor(){
				@Override
				public boolean onPlayerSubscriptionRequest(PlayerSubscriptionRequest request, ACLMessage aclMsg){
					// TODO: register the player if there is enough room.
					ACLMessage reply = aclMsg.createReply();
					/*FailureMessage response = new FailureMessage("Server not ready.");
					reply.setPerformative(ACLMessage.FAILURE);*/
					myAgent.send(reply);
					return true;
				}
			});
			
			if(!msgReceived)
				block();
			
		}

		@Override
		public boolean done() {
			// we stop waiting players when the game start:
			return gameStarted;
		}
		
	}

	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		switch (ServerGuiEvent.values()[arg0.getType()]) {
		case LAUNCH_SERVER:
			this.serverStarted = true;
			// when the server is started we start waiting players :
			addBehaviour(new waitServerToStartBhv());
			break;
		case LAUNCH_GAME:
			this.gameStarted = true;
			break;
		default:
			break;
		}
	}
}
