package sma.agent.simulationAgent;

import poker.game.model.BlindValueDefinition;
import poker.game.model.PlayersContainer;
import poker.game.model.Round;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import sma.agent.SimulationAgent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBehaviour;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBehaviour;
import sma.message.Message;
import sma.message.bet.request.BetRequest;

public class InitPreFlopBehaviour extends TaskRunnerBehaviour {

	SimulationAgent simulationAgent;
	AID betManagerAID;
	
	public InitPreFlopBehaviour(SimulationAgent simulationAgent) {
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
		
		this.betManagerAID = DFServiceHelper.searchService(simulationAgent, "BetManagerAgent", "BetManager");
	}

	@Override
	public void onStart(){
		System.out.println("@@@ InitPreFlopBehaviour @@@");
		
		simulationAgent.setCurrentRound(Round.PREFLOP);
		simulationAgent.resetRoundTableNumber();
		
		PlayersContainer playersContainer = this.simulationAgent.getGame().getPlayersContainer();
		
		AID smallBlindPlayerAID = playersContainer.getSmallBlind().getAID();
		AID bigBlindPlayerAID = playersContainer.getBigBlind().getAID();
		BlindValueDefinition blindValueDefinition = this.simulationAgent.getGame().getBlindValueDefinition();
				
		Task mainTask = Task.New(getBlindPaiementBehaviour(smallBlindPlayerAID, blindValueDefinition.getBlindAmountDefinition()))
				.then(getBlindPaiementBehaviour(bigBlindPlayerAID, blindValueDefinition.getBigBlindAmountDefinition()));
		
		setBehaviour(mainTask);
		super.onStart();
	}

	@Override
	public int onEnd(){
		return SimulationAgent.GameEvent.NEW_TABLE_ROUND.ordinal();
	}
	
	private Behaviour getBlindPaiementBehaviour(AID playerAID, int amount) {
		Message msg = new BetRequest(amount, playerAID);
		TransactionBehaviour transaction = new TransactionBehaviour(myAgent, msg, betManagerAID);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"player " + playerAID.getLocalName() + "paid blind.",
				"player " + playerAID.getLocalName() + " cant pay the blind."));		
		return transaction;
	}
}
