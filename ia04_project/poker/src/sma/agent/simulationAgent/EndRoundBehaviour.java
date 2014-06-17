package sma.agent.simulationAgent;

import poker.game.model.Round;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import sma.agent.SimulationAgent;
import sma.agent.SimulationAgent.GameEvent;
import sma.agent.helper.BlankBehaviour;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBehaviour;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBehaviour;
import sma.message.Message;
import sma.message.bet.request.MergeBetsRequest;
import sma.message.environment.request.SetDealerRequest;

public class EndRoundBehaviour extends TaskRunnerBehaviour {

	SimulationAgent simulationAgent;
	AID betMenager;

	GameEvent transition = GameEvent.NEW_ROUND;
	
	public EndRoundBehaviour(SimulationAgent simulationAgent) {
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
		this.betMenager = DFServiceHelper.searchService(simulationAgent, "BetManagerAgent", "BetManager");
	}
	
	@Override
	public void onStart(){
		Round round = this.simulationAgent.getCurrentRound();
		Task mainTask = Task.New(new BlankBehaviour());
		
		mainTask = mainTask.then(getMergeBetsBehaviour(getBetMenager()));
		
		if(round == Round.PLAYER_CARDS_DEAL){
			/*
			 * If the cards are dealt at the current round, we set the current round to PRE_FLOP, and we go back to init round
			 */
			simulationAgent.setCurrentRound(Round.PREFLOP);
		}
		else
		{
			/*
			 * Otherwise, we need to check if there is a dealer
			 */
			//TODO Check if there is a winner
			simulationAgent.setCurrentRound(Round.FLOP);
		}
		
		this.setBehaviour(mainTask);
		super.onStart();
	}
	
	private Behaviour getMergeBetsBehaviour(AID betManager) {
		Message msg = new MergeBetsRequest();
		TransactionBehaviour transaction = new TransactionBehaviour(myAgent, msg, betManager);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"Bets merged in pot.",
				"Bets could not be merged in pot."));		
		return transaction;
	}
	
	public AID getBetMenager() {
		return betMenager;
	}
	
	@Override
	public int onEnd(){
		return this.transition.ordinal();
	}
}
