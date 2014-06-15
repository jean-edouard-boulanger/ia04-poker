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
public class InitHandBhv extends TaskRunnerBhv {

	private AID environment;
	private AID dealerAgent;
	private AID betManager;
	private SimulationAgent simAgent;

	public InitHandBhv(SimulationAgent simAgent) {
		super(simAgent);
		this.simAgent = simAgent;
		this.environment = DFServiceHelper.searchService(simAgent, "PokerEnvironment", "Environment");
		this.dealerAgent = DFServiceHelper.searchService(simAgent, "DealerAgent","Dealer");
		this.betManager = DFServiceHelper.searchService(simAgent, "BetManagerAgent", "BetManager");
	}
	
	@Override
	public void onStart() {
		Game game = simAgent.getGame();
		
		simAgent.setCurrentRound(Round.PLAYER_CARDS_DEAL);

		Player dealer = getDealer(game.getPlayersContainer());

		Task mainTask = Task.New(setDealerBhv(dealer.getAID())) // first we set the dealer token
				.whenAll(communityCardResetBhv(), // then we remove community cards
						dealCardBhv())  // & deal cards to player (in parallel)
				.then(payBlindBhv(game.getPlayersContainer().getSmallBlind().getAID(), game.getBlindValueDefinition().getBlindAmountDefinition())) // we pay the small blind
				.then(payBlindBhv(game.getPlayersContainer().getBigBlind().getAID(),  game.getBlindValueDefinition().getBigBlindAmountDefinition())) // and the big blind
				.then(setCurrentPlayerBhv(game.getPlayersContainer())); // finally we set the current player (player next to the big blind, but not out
		
		setBehaviour(mainTask);
		
		super.onStart();
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

	private Behaviour dealCardBhv() {
		Message msg = new DealRequest(Round.PLAYER_CARDS_DEAL);
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

	private Behaviour payBlindBhv(AID blind, int amount) {
		Message msg = new BetRequest(amount, blind);
		TransactionBhv transaction = new TransactionBhv(myAgent, msg, betManager);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"player " + blind.getLocalName() + "paid blind.",
				"player " + blind.getLocalName() + " cant pay the blind."));		
		return transaction;
	}
	
	private Behaviour setCurrentPlayerBhv(PlayersContainer container){
		
		// we search the next player :		
		Iterator<Player> it = container.getCircularIterator(container.getPlayerNextTo(container.getBigBlind()));
		Player nextPlayer = it.next();
		while(nextPlayer.getStatus() == PlayerStatus.OUT)
			nextPlayer = it.next();
		
		// DEBUG:
		try {
			simAgent.getGame().getPlayersContainer().setCurrentPlayer(nextPlayer);
		} catch (NotRegisteredPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Message msg = new CurrentPlayerChangeRequest(nextPlayer.getAID());
		TransactionBhv transaction = new TransactionBhv(myAgent, msg, environment);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"player " + nextPlayer.getAID().getLocalName() + " will start the round.",
				"player " + nextPlayer.getAID().getLocalName() + " can't start the round :( "));		
		return transaction;
	}

	/**  Transition: return the NEW_ROUND transition code. */
	@Override
	public int onEnd(){
		return SimulationAgent.GameEvent.NEW_ROUND.ordinal();
	}

}
