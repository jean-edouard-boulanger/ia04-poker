package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.ParallelBehaviour;
import poker.game.player.model.Player;
import sma.agent.SimulationAgent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.Message;
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
		for (Player p : simAgent.getGame().getGamePlayers()){
			Message msg = new GiveTokenSetToPlayerRequest(simAgent.getDefaultTokenSet(), p.getAID());
			this.addSubBehaviour(new TransactionBhv(simAgent, msg, environment));
		}
	}
}
