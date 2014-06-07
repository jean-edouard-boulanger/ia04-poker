package sma.agent.simAgent;

import sma.agent.SimAgent;
import jade.core.behaviours.Behaviour;

public class InitHandBhv extends Behaviour {
	
	private SimAgent simAgent;

	public InitHandBhv(SimAgent simAgent) {
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
	 * Return the NEW_STEP transition code.
	 */
	@Override
	public int onEnd(){
		return SimAgent.GameEvent.NEW_ROUND.ordinal();
		
	}

}
