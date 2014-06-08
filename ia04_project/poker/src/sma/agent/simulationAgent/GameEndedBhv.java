package sma.agent.simulationAgent;

import sma.agent.SimulationAgent;
import jade.core.behaviours.Behaviour;

/**
 * FSM Final State.
 * Start when there is a final winner.
 */
public class GameEndedBhv extends Behaviour {
	
	private SimulationAgent simAgent;

	public GameEndedBhv(SimulationAgent simAgent) {
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

}
