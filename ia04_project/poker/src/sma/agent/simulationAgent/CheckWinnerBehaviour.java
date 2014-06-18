package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;

import sma.agent.SimulationAgent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBehaviour;
import sma.message.MessageVisitor;
import sma.message.determine_winner.DetermineWinnerRequest;
import sma.message.environment.notification.WinnerDeterminedNotification;

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
		
		DetermineWinnerRequest determineWinnerRequest = new DetermineWinnerRequest();
		
		TransactionBehaviour transaction = new TransactionBehaviour(myAgent, determineWinnerRequest, determineWinnerAgent);
		transaction.setResponseVisitor(new MessageVisitor(){
			public boolean onWinnerDeterminedNotification(WinnerDeterminedNotification notification, ACLMessage aclMsg) {
				
				simulationAgent.setWinners((HashMap)notification.getWinners());
				
				return true;
			}
		});

	}

	@Override
	public boolean done() {
		return false;
	}
	
}
