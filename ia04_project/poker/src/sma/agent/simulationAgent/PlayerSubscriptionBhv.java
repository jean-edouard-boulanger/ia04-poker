package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import poker.game.player.model.Player;
import sma.agent.SimulationAgent;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.PlayerSubscriptionRequest;
import sma.message.environment.request.AddPlayerTableRequest;

/**
 * This behavior wait player subscriptions.
 * The behavior stop when the user click 'Start the game' in the server gui.
 */
public class PlayerSubscriptionBhv extends CyclicBehaviour
{
    private SimulationAgent simAgent;

    public PlayerSubscriptionBhv(SimulationAgent agent){
	super(agent);
	this.simAgent = agent;
    }

    /**
     * Register players if there is enough room (only if the server is started and the game not running).
     */
    @Override
    public void action() {

	boolean msgReceived = AgentHelper.receiveMessage(this.myAgent, ACLMessage.SUBSCRIBE, new MessageVisitor(){
	    @Override
	    public boolean onPlayerSubscriptionRequest(PlayerSubscriptionRequest request, ACLMessage aclMsg){

		if(!simAgent.isServerStarted()){
		    AgentHelper.sendReply(myAgent, aclMsg, ACLMessage.FAILURE, new FailureMessage("Server not ready."));
		}
		else if (simAgent.isGameStarted()){
		    AgentHelper.sendReply(myAgent, aclMsg, ACLMessage.FAILURE, new FailureMessage("Game already started."));
		}
		else if (simAgent.getGame().getPlayersContainer().getPlayers().size() >= simAgent.getMaxPlayers()){
		    AgentHelper.sendReply(myAgent, aclMsg, ACLMessage.FAILURE, new FailureMessage("Game full."));
		}
		else if (simAgent.getGame().getPlayersContainer().getPlayerByName(request.getPlayerName()) != null){
		    AgentHelper.sendReply(myAgent, aclMsg, ACLMessage.FAILURE, new FailureMessage("Pseudo already taken."));
		}
		else {
		    // we subscribe the player to the environment (async).
		    Player player = new Player(aclMsg.getSender(), request.getPlayerName());
		    AID environment = DFServiceHelper.searchService(simAgent, "PokerEnvironment", "Environment");
		    simAgent.addBehaviour(new TransactionBhv(simAgent, new AddPlayerTableRequest(player), environment));
		    AgentHelper.sendReply(simAgent, aclMsg, ACLMessage.INFORM, new OKMessage());
		}
		return true;
	    }
	});

	if(!msgReceived)
	    block();

    }

}