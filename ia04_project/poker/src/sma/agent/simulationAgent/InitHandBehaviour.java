package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;

import java.util.Iterator;

import poker.game.exception.NotRegisteredPlayerException;
import poker.game.model.Game;
import poker.game.model.PlayersContainer;
import poker.game.model.Round;
import poker.game.player.model.Player;
import poker.game.player.model.PlayerStatus;
import sma.agent.SimulationAgent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBhv;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBhv;
import sma.message.Message;
import sma.message.bet.request.BetRequest;
import sma.message.dealer.request.DealRequest;
import sma.message.environment.request.CurrentPlayerChangeRequest;
import sma.message.environment.request.EmptyCommunityCardsRequest;
import sma.message.environment.request.SetDealerRequest;

/**
 * The behavior handle the initialization of a new game:
 * - community cards are removed from the table.
 * - player card are dealt
 * - we set the dealer
 * - blinds are payed
 */
public class InitHandBehaviour extends TaskRunnerBhv {

	private AID environment;
	private AID dealerAgent;
	private AID betManager;
	private SimulationAgent simulationAgent;

	public InitHandBehaviour(SimulationAgent simulationAgent) {
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
		this.environment = DFServiceHelper.searchService(simulationAgent, "PokerEnvironment", "Environment");
		this.dealerAgent = DFServiceHelper.searchService(simulationAgent, "DealerAgent","Dealer");
		this.betManager = DFServiceHelper.searchService(simulationAgent, "BetManagerAgent", "BetManager");
	}
	
	@Override
	public void onStart() {
		
		System.out.println("@@@ InitHandBehaviour @@@");
		
		Game game = simulationAgent.getGame();
		
		simulationAgent.setCurrentRound(Round.PLAYER_CARDS_DEAL);
		simulationAgent.resetRoundTableNumber();
		
		Player dealer = this.getDealer();

		Task mainTask = Task.New(getDealerSetterBehaviour(dealer.getAID())) // first we set the dealer token
				.then(dealCardBhv());
		
		setBehaviour(mainTask);
		
		super.onStart();
	}

	private Player getDealer() {
		PlayersContainer playersContainer = this.simulationAgent.getGame().getPlayersContainer();
		
		Player player = playersContainer.getDealer();
		if(player == null){
			player = playersContainer.getRandomPlayer();
			try {
				playersContainer.setDealer(player);
			} catch (NotRegisteredPlayerException e) {
				e.printStackTrace();
			}
		}
		return player;
	}

	private Behaviour dealCardBhv() {
		Message msg = new DealRequest(Round.PLAYER_CARDS_DEAL);
		TransactionBhv transaction = new TransactionBhv(myAgent, msg, dealerAgent);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"cards dealt successfully.",
				"error while dealing cards."));		
		return transaction;
	}

	private Behaviour getDealerSetterBehaviour(AID dealer) {
		Message msg = new SetDealerRequest(dealer);
		TransactionBhv transaction = new TransactionBhv(myAgent, msg, environment);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"dealer set successfuly successfully to " + dealer.getLocalName() + ".",
				"error setting dealer to " + dealer.getLocalName() + "."));		
		
		return transaction;
	}


	/**  Transition: return the START_PRE_FLOP transition code. */
	@Override
	public int onEnd(){
		return SimulationAgent.GameEvent.START_PRE_FLOP.ordinal();
	}
}
