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
import sma.agent.helper.TransactionBehaviour;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBehaviour;
import sma.message.Message;
import sma.message.bet.request.BetRequest;
import sma.message.dealer.request.DealRequest;
import sma.message.environment.request.CurrentPlayerChangeRequest;
import sma.message.environment.request.EmptyCardsRequest;
import sma.message.environment.request.SetDealerRequest;

/**
 * The behavior handle the initialization of a new game:
 * - community cards are removed from the table.
 * - player card are dealt
 * - we set the dealer
 * - blinds are payed
 */
public class InitHandBehaviour extends TaskRunnerBehaviour {

	private AID environment;
	private SimulationAgent simulationAgent;

	public InitHandBehaviour(SimulationAgent simulationAgent) {
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
		this.environment = DFServiceHelper.searchService(simulationAgent, "PokerEnvironment", "Environment");
	}
	
	@Override
	public void onStart() {
		
		System.out.println("@@@ InitHandBehaviour @@@");
		
		simulationAgent.setCurrentRound(Round.PLAYER_CARDS_DEAL);
		simulationAgent.resetRoundTableNumber();
		
		Player dealer = this.getDealer();

		// The only thing to do at the beginning of a new hand is to set the dealer
		Task mainTask = Task.New(getDealerSetterBehaviour(dealer.getAID()));
		
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
		else {
			player = playersContainer.getInGamePlayerNextTo(player);
		}
		return player;
	}

	private Behaviour getDealerSetterBehaviour(AID dealer) {
		Message msg = new SetDealerRequest(dealer);
		TransactionBehaviour transaction = new TransactionBehaviour(myAgent, msg, environment);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"dealer set successfuly successfully to " + dealer.getLocalName() + ".",
				"error setting dealer to " + dealer.getLocalName() + "."));		
		return transaction;
	}

	@Override
	public int onEnd(){
		return SimulationAgent.GameEvent.NEW_ROUND.ordinal();
	}
}
