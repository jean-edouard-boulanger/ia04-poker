package sma.agent.simulationAgent;

import sma.agent.SimulationAgent;
import jade.core.behaviours.Behaviour;

public class PlayBhv extends Behaviour {
	
	private SimulationAgent simAgent;

	public PlayBhv(SimulationAgent simAgent) {
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
	 * Return the transition code, either PLAY or STEP_ENDED.
	 */
	@Override
	public int onEnd(){
		if(checkIfRoundFinished())
			return SimulationAgent.GameEvent.ROUND_ENDED.ordinal();
		else
			return SimulationAgent.GameEvent.PLAY.ordinal();
		
	}
	
	/**
	 * Check if the current round (pre-flop, flop, river or turn) is finished meaning
	 * that everyone called or folded (except for the pre-flop where the big blind can either choose
	 * to check or to raise after every called or folded).
	 * @return true if the round is finished.
	 */
	private boolean checkIfRoundFinished() {
		// TODO Auto-generated method stub
		return false;
	}

}
