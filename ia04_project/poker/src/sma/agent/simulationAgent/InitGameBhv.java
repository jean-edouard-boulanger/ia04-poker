package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import poker.game.player.model.Player;
import sma.agent.SimulationAgent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.environment.request.GiveTokenSetToPlayerRequest;

/**
 * Start a new game, give every players a token set.
 */
public class InitGameBhv extends ParallelBehaviour {

	public InitGameBhv(SimulationAgent simAgent) {
		// The parallel behavior give every players a tokenSet (based
		// the default distribution in simAgent).
		super(simAgent, ParallelBehaviour.WHEN_ALL);
		AID environment = DFServiceHelper.searchService(simAgent, "PokerEnvironment", "Environment");
		for (Player p : simAgent.getGame().getPlayersContainer().getPlayers()){
			Message msg = new GiveTokenSetToPlayerRequest(simAgent.getDefaultTokenSet(), p.getAID());
			TransactionBhv transaction = new TransactionBhv(simAgent, msg, environment);
			transaction.setResponseVisitor(new MessageVisitor(){
				@Override
				public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
					System.out.println("[" + myAgent.getLocalName() + "] initial token set given to player.");
					return true;
				}
				@Override
				public boolean onFailureMessage(FailureMessage msg,ACLMessage aclMsg) {
					System.out.println("[" + myAgent.getLocalName() + "] erro while giving initial token set to player: " + msg.getMessage());
					return true;
				}
			});
			this.addSubBehaviour(transaction);
		}
	}
}
