package sma.agent.simulationAgent;

import sma.agent.SimulationAgent;
import jade.core.behaviours.Behaviour;

public class CheckWinnerBhv extends Behaviour {
	
	private SimulationAgent simAgent;
	private boolean gameEnded;
	private boolean handEnded;

	public CheckWinnerBhv(SimulationAgent simAgent) {
		super(simAgent);
		this.simAgent = simAgent;
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Transition: 
	 * Return the transition code, either GAME_FINISHED, NEW_HAND or NEW_ROUND.
	 */
	@Override
	public int onEnd(){
		if(this.gameEnded) // there is a final winner
			return SimulationAgent.GameEvent.GAME_FINISHED.ordinal();
		if(this.handEnded)// there is a hand winner
			return SimulationAgent.GameEvent.NEW_HAND.ordinal();
		else 
			return SimulationAgent.GameEvent.NEW_ROUND.ordinal();
	}

}
