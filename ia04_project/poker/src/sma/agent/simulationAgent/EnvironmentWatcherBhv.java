package sma.agent.simulationAgent;

import javax.management.Notification;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
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
import sma.message.SubscriptionOKMessage;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.request.AddPlayerTableRequest;

/**
 * This behavior wait player subscriptions.
 * The behavior stop when the user click 'Start the game' in the server gui.
 */
public class EnvironmentWatcherBhv extends CyclicBehaviour
{
	private SimulationAgent simAgent;
	private AID environment;
	
	public EnvironmentWatcherBhv(SimulationAgent agent){
		super(agent);
		this.simAgent = agent;
		this.environment = DFServiceHelper.searchService(simAgent,"PokerEnvironment", "Environment");
		subscribeToEnvironment();
	}
	
	private void subscribeToEnvironment(){
		
		TransactionBhv envSubscriptionBhv = new TransactionBhv(simAgent, null, environment, ACLMessage.SUBSCRIBE);
		envSubscriptionBhv.setResponseVisitor(new MessageVisitor(){
			@Override
			public boolean onSubscriptionOKMessage(SubscriptionOKMessage msg, ACLMessage aclMsg) {
				System.out.println("[" + simAgent.getLocalName() + "] subscription to environment succeded.");
				simAgent.setGame(msg.getGame());
				if(simAgent.getGame().getPlayersAIDs() != null)
					System.out.println("[" + simAgent.getLocalName() + "] " + simAgent.getGame().getPlayersAIDs().size()  + " already added.");
				return true;
			}
			
			@Override
			public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
				System.out.println("[" + simAgent.getLocalName() + "] subscription to environment failed: " + msg.getMessage());
				return true;
			}
		});
		simAgent.addBehaviour(envSubscriptionBhv);
	}
	
	/**
	 * Handle environment events.
	 */
	@Override
	public void action() {
		
		//TEST:
		boolean msgReceived = AgentHelper.receiveMessage(this.myAgent,MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE), new MessageVisitor(){
			@Override
			public boolean onPlayerSitOnTableNotification(PlayerSitOnTableNotification notification, ACLMessage aclMsg) {
				try {
					simAgent.getGame().addPlayer(notification.getNewPlayer());
					System.out.println("[" + simAgent.getLocalName() + "] player " + notification.getNewPlayer().getPlayerName() + " added.");
				} catch (PlayerAlreadyRegisteredException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
			@Override
			public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		
		if(!msgReceived)
			block();
		
	}
	
}