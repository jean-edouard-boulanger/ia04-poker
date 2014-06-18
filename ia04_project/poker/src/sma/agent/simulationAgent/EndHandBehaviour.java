package sma.agent.simulationAgent;

import jade.core.Agent;
import sma.agent.SimulationAgent;
import sma.agent.helper.experimental.TaskRunnerBehaviour;

public class EndHandBehaviour extends TaskRunnerBehaviour {

	private SimulationAgent simulationAgent;
	
	public EndHandBehaviour(SimulationAgent agent) {
		super(agent);
		this.simulationAgent = simulationAgent;
	}
}
