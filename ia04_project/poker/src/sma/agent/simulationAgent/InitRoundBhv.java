package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import poker.game.model.Round;
import sma.agent.SimulationAgent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBhv;
import sma.agent.helper.experimental.TaskRunnerBhv;
import sma.message.Message;
import sma.message.dealer.request.DealRequest;

public class InitRoundBhv extends TaskRunnerBhv {

    private AID dealerAgent;

    public InitRoundBhv(SimulationAgent simAgent) {
	super(simAgent);
	this.dealerAgent = DFServiceHelper.searchService(simAgent, "DealerAgent","Dealer");
    }
    
    private Behaviour dealCommunityCardsBhv(Round round) {
	Message msg = new DealRequest(round);
	TransactionBhv transaction = new TransactionBhv(myAgent, msg, dealerAgent);
	transaction.setResponseVisitor(new SimpleVisitor(myAgent,
		"community cards dealt successfully.",
		"error while dealing community cards."));		
	return transaction;
    }
        
    /**
     * Transition: 
     * Return the PLAY transition code.
     */
    @Override
    public int onEnd(){
	return SimulationAgent.GameEvent.PLAY.ordinal();

    }

}
