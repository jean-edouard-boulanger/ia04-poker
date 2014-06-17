package sma.agent.simulationAgent;

import sma.agent.SimulationAgent;
import jade.core.behaviours.Behaviour;

public class TableRoundEndBehaviour extends Behaviour {

	SimulationAgent simulationAgent;
	
	public TableRoundEndBehaviour(SimulationAgent simulationAgent){
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
	}
	
	@Override
	public void action() {
		
	}

	@Override
	public boolean done() {
		return false;
	}
}
