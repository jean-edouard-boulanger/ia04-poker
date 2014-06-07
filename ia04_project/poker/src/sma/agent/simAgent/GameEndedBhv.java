package sma.agent.simAgent;

import sma.agent.SimAgent;
import jade.core.behaviours.Behaviour;

/**
 * FSM Final State.
 * Start when there is a final winner.
 */
public class GameEndedBhv extends Behaviour {
	
	private SimAgent simAgent;

	public GameEndedBhv(SimAgent simAgent) {
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
