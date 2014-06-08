package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.player.model.Player;
import sma.agent.SimulationAgent;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.PlayerSubscriptionRequest;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.request.AddPlayerTableRequest;

/**
 * This behavior wait player subscriptions.
 * The behavior stop when the user click 'Start the game' in the server gui.
 */
public class EnvironmentWatcherBhv extends CyclicBehaviour
{
	private SimulationAgent simAgent;
	
	public EnvironmentWatcherBhv(SimulationAgent agent){
		super(agent);
		this.simAgent = agent;
		subscribeToEnvironment();
	}
	
	private void subscribeToEnvironment(){
		//TODO: replace OKMessage by a real subscription message.
		AgentHelper.sendSimpleMessage(simAgent,DFServiceHelper.searchService(simAgent,"PokerEnvironment", "Environment"), ACLMessage.SUBSCRIBE, new OKMessage());
	}
	
	/**
	 * Handle environment events.
	 */
	@Override
	public void action() {
			
		boolean msgReceived = AgentHelper.receiveMessage(this.myAgent, ACLMessage.PROPAGATE, new MessageVisitor(){
			@Override
			public boolean onPlayerSitOnTableNotification(PlayerSitOnTableNotification notification, ACLMessage aclMsg) {
				try {
					simAgent.getGame().addPlayer(notification.getNewPlayer());
				} catch (PlayerAlreadyRegisteredException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
		});
		
		if(!msgReceived)
			block();
		
	}
	
}