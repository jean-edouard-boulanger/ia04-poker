package sma.agent.simulationAgent;

import poker.game.model.Round;
import jade.core.Agent;
import sma.agent.SimulationAgent;
import sma.agent.SimulationAgent.GameEvent;
import sma.agent.helper.BlankBehaviour;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBehaviour;

public class EndRoundBehaviour extends TaskRunnerBehaviour {

	SimulationAgent simulationAgent;
	GameEvent transition = GameEvent.NEW_ROUND;
	
	public EndRoundBehaviour(SimulationAgent simulationAgent) {
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
	}
	
	@Override
	public void onStart(){
		Round round = this.simulationAgent.getCurrentRound();
		Task mainTask = Task.New(new BlankBehaviour());
		
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
	
	@Override
	public int onEnd(){
		return this.transition.ordinal();
	}
}
