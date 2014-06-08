package sma.agent.simulationAgent;

import sma.agent.SimulationAgent;
import jade.core.behaviours.Behaviour;

public class InitRoundBhv extends Behaviour {
	
	private SimulationAgent simAgent;

	public InitRoundBhv(SimulationAgent simAgent) {
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
	 * Return the PLAY transition code.
	 */
	@Override
	public int onEnd(){
		return SimulationAgent.GameEvent.PLAY.ordinal();
		
	}

}
