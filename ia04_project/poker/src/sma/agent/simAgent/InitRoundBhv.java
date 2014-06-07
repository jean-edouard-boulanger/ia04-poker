package sma.agent.simAgent;

import sma.agent.SimAgent;
import jade.core.behaviours.Behaviour;

public class InitRoundBhv extends Behaviour {
	
	private SimAgent simAgent;

	public InitRoundBhv(SimAgent simAgent) {
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
		return SimAgent.GameEvent.PLAY.ordinal();
		
	}

}
