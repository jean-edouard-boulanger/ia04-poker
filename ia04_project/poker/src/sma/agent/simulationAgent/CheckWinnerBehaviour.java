package sma.agent.simulationAgent;

import sma.agent.SimulationAgent;
import jade.core.behaviours.Behaviour;

public class CheckWinnerBehaviour extends Behaviour {
	
	SimulationAgent simulationAgent;

	public CheckWinnerBehaviour(SimulationAgent simulationAgent){
		this.simulationAgent = simulationAgent;
	}
	
	@Override
	public void action() {
		/*
		 * Ajouter un winner avec
		 * this.simulationAgent.addHandWinner(player);
		 */
	}

	@Override
	public boolean done() {
		return false;
	}
	
}
