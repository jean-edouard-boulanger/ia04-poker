package sma.agent.simAgent;

import sma.agent.SimulationAgent;
import jade.core.behaviours.Behaviour;

public class InitHandBhv extends Behaviour {
	
	private SimulationAgent simAgent;

	public InitHandBhv(SimulationAgent simAgent) {
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
		return SimulationAgent.GameEvent.NEW_ROUND.ordinal();
		
	}

}
