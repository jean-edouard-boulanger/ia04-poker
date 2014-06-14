package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import poker.game.exception.NotRegisteredPlayerException;
import poker.game.model.HandStep;
import poker.game.model.PlayersContainer;
import poker.game.player.model.Player;
import sma.agent.SimulationAgent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBhv;
import sma.agent.helper.experimental.Task;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.dealer.request.DealRequest;
import sma.message.environment.request.EmptyCommunityCardsRequest;
import sma.message.environment.request.GiveTokenSetToPlayerRequest;
import sma.message.environment.request.SetDealerRequest;

/**
 * The behavior handle the initialization of a new game:
 * - community cards are removed from the table.
 * - player card are dealt
 * - we set the dealer
 * (those tasks are done in parallel).
 */
public class InitHandBhv extends SequentialBehaviour {

    private AID environment;
    private AID dealerAgent;

    public InitHandBhv(SimulationAgent simAgent) {
	super(simAgent);

	this.environment = DFServiceHelper.searchService(simAgent, "PokerEnvironment", "Environment");
	this.dealerAgent = DFServiceHelper.searchService(simAgent, "DealerAgent","Dealer");
	
	simAgent.setCurrentRound(Round.preflop);

	Player dealer = getDealer(simAgent.getGame().getPlayersContainer());
	
	Task mainTask = Task.New(communityCardResetBhv())
		.then(cardDistributionBhv())
		.then(setDealerBhv(dealer.getAID()));
	
	this.addSubBehaviour(mainTask);
	
	
    }

    private Player getDealer(PlayersContainer container) {
	// we determine the current, if no dealer is set we choose the first player:
	if(container.getDealer() == null) {
	    try {
		container.setDealer(container.getPlayersAIDs().get(0));
	    } catch (NotRegisteredPlayerException e) {
		e.printStackTrace(); //TODO: handle exceptions
	    }
	}
	else{
	    try {
		container.setDealer(container.getPlayerNextTo(container.getDealer()));
	    } catch (NotRegisteredPlayerException e) {
		e.printStackTrace(); // TODO Auto-generated catch block
	    }
	}
	return container.getDealer();
    }

    private Behaviour cardDistributionBhv() {
	Message msg = new DealRequest(HandStep.PLAYER_CARDS_DEAL);
	TransactionBhv transaction = new TransactionBhv(myAgent, msg, dealerAgent);
	transaction.setResponseVisitor(new SimpleVisitor(myAgent,
		"cards dealt successfully.",
		"error while dealing cards."));		
	return transaction;
    }

    private Behaviour communityCardResetBhv() {
	Message msg = new EmptyCommunityCardsRequest();
	TransactionBhv transaction = new TransactionBhv(myAgent, msg, environment);
	transaction.setResponseVisitor(new SimpleVisitor(myAgent,
		"community cards emptied successfully.",
		"error while cleaning community cards"));
	return transaction;
    }

    private Behaviour setDealerBhv(AID dealer) {
	Message msg = new SetDealerRequest(dealer);
	TransactionBhv transaction = new TransactionBhv(myAgent, msg, environment);
	transaction.setResponseVisitor(new SimpleVisitor(myAgent,
		"dealer set successfuly successfully to " + dealer.getLocalName() + ".",
		"error setting dealer to " + dealer.getLocalName() + "."));		
	return transaction;
    }

    /**  Transition: return the NEW_ROUND transition code. */
    @Override
    public int onEnd(){
	return SimulationAgent.GameEvent.NEW_ROUND.ordinal();
    }

}
