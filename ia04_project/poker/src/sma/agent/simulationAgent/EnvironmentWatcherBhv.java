package sma.agent.simulationAgent;

import javax.management.Notification;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import poker.game.exception.NoPlaceAvailableException;
import poker.game.exception.NotRegisteredPlayerException;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.player.model.Player;
import sma.agent.SimulationAgent;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBehaviour;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.PlayerSubscriptionRequest;
import sma.message.SubscriptionOKMessage;
import sma.message.environment.notification.BlindValueDefinitionChangedNotification;
import sma.message.environment.notification.CurrentPlayerChangedNotification;
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

	TransactionBehaviour envSubscriptionBhv = new TransactionBehaviour(simAgent, null, environment, ACLMessage.SUBSCRIBE);
	envSubscriptionBhv.setResponseVisitor(new MessageVisitor(){

	    @Override
	    public boolean onSubscriptionOK(SubscriptionOKMessage msg, ACLMessage aclMsg) {
		System.out.println("[" + simAgent.getLocalName() + "] subscription to environment succeded.");
		simAgent.setGame(msg.getGame());
		if(simAgent.getGame().getPlayersContainer().getPlayersAIDs() != null)
		    System.out.println("[" + simAgent.getLocalName() + "] " + simAgent.getGame().getPlayersContainer().getPlayersAIDs().size()  + " already added.");
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
	
	boolean msgReceived = AgentHelper.receiveMessage(this.myAgent,MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE), new MessageVisitor(){
	    
	    @Override
	    public boolean onPlayerSitOnTableNotification(PlayerSitOnTableNotification notification, ACLMessage aclMsg) {
		try {
		    simAgent.getGame().getPlayersContainer().addPlayer(notification.getNewPlayer());
		    System.out.println("[" + simAgent.getLocalName() + "] player " + notification.getNewPlayer().getNickname() + " added.");
		} catch (PlayerAlreadyRegisteredException e) {
		    // TODO Auto-generated catch block

		    e.printStackTrace();
		} catch (NoPlaceAvailableException e) {
		    // TODO Auto-generated catch block

		    e.printStackTrace();
		}
		return true;
	    }
	    
	    @Override
	    public boolean onBlindValueDefinitionChangedNotification(BlindValueDefinitionChangedNotification notif, ACLMessage aclMsg) {
	    	simAgent.getGame().setBlindValueDefinition(notif.getNewBlindValueDefinition());
	        return true;
	    }
	    
	    @Override
	    public boolean onCurrentPlayerChangedNotification(CurrentPlayerChangedNotification notification, ACLMessage aclMsg) {
	    	Player p = simAgent.getGame().getPlayersContainer().getPlayerByAID(notification.getPlayerAID());
	    	try {
				simAgent.getGame().getPlayersContainer().setCurrentPlayer(p);
			} catch (NotRegisteredPlayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return true;
	    }

	    // All other environment changes are discarded.
	    @Override
	    public boolean onEnvironmentChanged(Message notif, ACLMessage aclMsg) {	return true; }
	});

	if(!msgReceived)
	    block();

    }

}