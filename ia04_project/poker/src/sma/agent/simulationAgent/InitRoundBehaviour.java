package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import poker.game.model.Game;
import poker.game.model.Round;
import sma.agent.SimulationAgent;
import sma.agent.SimulationAgent.GameEvent;
import sma.agent.helper.BlankBehaviour;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBehaviour;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBehaviour;
import sma.message.Message;
import sma.message.bet.request.BetRequest;
import sma.message.dealer.request.DealRequest;

public class InitRoundBehaviour extends TaskRunnerBehaviour {

	private AID dealerAID;
	private AID betManagerAID;
	
	private SimulationAgent simulationAgent;
	private GameEvent transition = GameEvent.NEW_TABLE_ROUND;
	
	public InitRoundBehaviour(SimulationAgent simulationAgent) {
		super(simulationAgent);
		
		this.dealerAID = DFServiceHelper.searchService(simulationAgent, "DealerAgent","Dealer");
		this.betManagerAID = DFServiceHelper.searchService(simulationAgent, "BetManagerAgent", "BetManager");

		this.simulationAgent = simulationAgent;
	}

	@Override
	public void onStart() {
		
		Round round = this.simulationAgent.getCurrentRound();
		
		System.out.println("@@@ InitRoundBehaviour @@@ " + round);
		
		Task mainTask = Task.New(new BlankBehaviour());
		
		if(round == Round.PLAYER_CARDS_DEAL){
			/*
			 * If we are currently in the first round (Deal cards), we only need to deal the cards
			 */
			mainTask.then(this.getDealPlayersCardsBehaviour());
			this.transition = GameEvent.END_ROUND;
		}
		else if(round == Round.PREFLOP){
			/* 
			 * If we are currently in the pre-flop, we need to request the big-blind and the small blind players (Through the BetManagerAgent)
			 * to pay the blind
			 */
			
			Game game = this.simulationAgent.getGame();
			
			int smallBlindAmount = game.getBlindValueDefinition().getBlindAmountDefinition();
			int bigBlindAmount = game.getBlindValueDefinition().getBigBlindAmountDefinition();
			
			mainTask = mainTask.then(getBlindPaiementBehaviour(game.getPlayersContainer().getSmallBlind().getAID(), smallBlindAmount));
			mainTask = mainTask.then(getBlindPaiementBehaviour(game.getPlayersContainer().getBigBlind().getAID(), bigBlindAmount));
			
			mainTask = mainTask.then(getDealPlayersCardsBehaviour());
		}
		else if(round == Round.RIVER || round == Round.TURN || round == round.FLOP){
			/*
			 * If we are at the flop, river or turn, we need to deal 3 (Flop) or 1 (River + Turn) cards. That information is known by the DealerAgent
			 */
			mainTask = mainTask.then(this.getDealCommunityCardsBehaviour(round));
		}
		else{
			/*
			 * This behaviour only deals with the 5 game steps, prints an error
			 */
			System.err.println("SEVERE [SimulationAgent.initRoundBehaviour] Was called when the hand round was: " + round + "(I don't deal that kind of round)");
		}
		
		this.setBehaviour(mainTask);
		super.onStart();
	}

	private Behaviour getDealPlayersCardsBehaviour() {
		Message msg = new DealRequest(Round.PLAYER_CARDS_DEAL);
		TransactionBehaviour transaction = new TransactionBehaviour(myAgent, msg, dealerAID);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"cards dealt successfully.",
				"error while dealing cards."));		
		return transaction;
	}
	
	private Behaviour getDealCommunityCardsBehaviour(Round round) {
		Message msg = new DealRequest(round);
		TransactionBehaviour transaction = new TransactionBehaviour(myAgent, msg, dealerAID);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"community cards dealt successfully.",
				"error while dealing community cards."));		
		return transaction;
	}
	
	private Behaviour getBlindPaiementBehaviour(AID playerAID, int amount) {
		Message msg = new BetRequest(amount, playerAID);
		TransactionBehaviour transaction = new TransactionBehaviour(myAgent, msg, betManagerAID);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"player " + playerAID.getLocalName() + "paid blind.",
				"player " + playerAID.getLocalName() + " cant pay the blind."));		
		return transaction;
	}
	

	@Override
	public int onEnd(){
		return this.transition.ordinal();
	}
}
