package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;

import poker.card.heuristics.combination.model.Hand;
import sma.agent.SimulationAgent;
import sma.agent.SimulationAgent.GameEvent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBehaviour;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBehaviour;
import sma.message.MessageVisitor;
import sma.message.determine_winner.DetermineWinnerRequest;
import sma.message.environment.notification.WinnerDeterminedNotification;

public class CheckWinnerBehaviour extends TaskRunnerBehaviour {
	
	SimulationAgent simulationAgent;
	AID determineWinnerAgent;
	int step;
	
	public CheckWinnerBehaviour(SimulationAgent simulationAgent){
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
		this.determineWinnerAgent = DFServiceHelper.searchService(simulationAgent, "DetermineWinnerAgent", "DetermineWinner");
	}
	
	
	@Override
	public void onStart(){
		
		System.out.println("@@@ CheckWinnerBehaviour @@@");
		
		Task mainTask = Task.New(this.getDetermineWinnerBehaviour());
		this.setBehaviour(mainTask);
		super.onStart();
	}
	
	@Override
	public int onEnd(){
		return GameEvent.END_HAND.ordinal();
	}	
	
	private TransactionBehaviour getDetermineWinnerBehaviour(){
		DetermineWinnerRequest determineWinnerRequest = new DetermineWinnerRequest();
		
		TransactionBehaviour transaction = new TransactionBehaviour(myAgent, determineWinnerRequest, determineWinnerAgent);
		transaction.setResponseVisitor(new MessageVisitor(){
			public boolean onWinnerDeterminedNotification(WinnerDeterminedNotification notification, ACLMessage aclMsg) {	
				
				simulationAgent.setWinners((HashMap)notification.getWinners());
				
				return true;
			}
		});
		return transaction;
	}
}
