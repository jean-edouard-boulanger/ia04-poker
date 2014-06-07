package sma.agent.simAgent;

import sma.agent.SimAgent;
import jade.core.behaviours.Behaviour;

public class CheckWinnerBhv extends Behaviour {
	
	private SimAgent simAgent;
	private boolean gameEnded;
	private boolean handEnded;

	public CheckWinnerBhv(SimAgent simAgent) {
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
			return SimAgent.GameEvent.GAME_FINISHED.ordinal();
		if(this.handEnded)// there is a hand winner
			return SimAgent.GameEvent.NEW_HAND.ordinal();
		else 
			return SimAgent.GameEvent.NEW_ROUND.ordinal();
	}

}
