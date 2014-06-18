package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import sma.agent.SimulationAgent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBehaviour;

public class CheckWinnerBehaviour extends Behaviour {
	
	SimulationAgent simulationAgent;
	AID determineWinnerAgent;
	
	public CheckWinnerBehaviour(SimulationAgent simulationAgent){
		this.simulationAgent = simulationAgent;
		this.determineWinnerAgent = DFServiceHelper.searchService(simulationAgent, "DetermineWinnerAgent", "DetermineWinner");
	}
	
	@Override
	public void action() {
		/*
		 * Ajouter un winner avec
		 * this.simulationAgent.addHandWinner(player);
		 */
		
		TransactionBehaviour transaction = new TransactionBehaviour(myAgent, msg, environment);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"token set given to player " + player.getLocalName() +".",
				"error while giving token set to player " + player.getLocalName() +"."));

	}

	@Override
	public boolean done() {
		return false;
	}
	
}
