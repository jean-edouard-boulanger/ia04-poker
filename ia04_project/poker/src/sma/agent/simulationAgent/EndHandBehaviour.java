package sma.agent.simulationAgent;


import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import poker.card.heuristics.combination.model.Hand;
import poker.game.player.model.Player;
import sma.agent.SimulationAgent;
import sma.agent.SimulationAgent.GameEvent;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBehaviour;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBehaviour;
import sma.message.bet.request.DistributePotToWinnersRequest;
import sma.message.environment.notification.WinnerDeterminedNotification;
import sma.message.environment.request.EmptyCardsRequest;
import sma.message.environment.request.EmptyPotRequest;

public class EndHandBehaviour extends TaskRunnerBehaviour {

	private SimulationAgent simulationAgent;
	private AID environmentAID;
	private AID betmanagerAID;
	
	public EndHandBehaviour(SimulationAgent simulationAgent) {
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
		this.environmentAID = DFServiceHelper.searchService(simulationAgent, "PokerEnvironment", "Environment");
		this.betmanagerAID = DFServiceHelper.searchService(simulationAgent, "BetManagerAgent", "BetManager");
	}
	
	public void onStart(){
		
		System.out.println("@@@ EndHandBehaviour @@@");
		
		Task mainTask = Task.New(this.getEmptyCommunityCardsBehaviour())
				.then(this.getEmptyPotBehaviour())
				.then(new OneShotBehaviour() {
					@Override
					public void action() {
						Map<AID, Hand> winnersAIDS = new HashMap<AID, Hand>();
						for(Map.Entry<AID, Hand> winnerAID : simulationAgent.getWinners().entrySet()){
							winnersAIDS.put(winnerAID.getKey(), winnerAID.getValue());
						}
						WinnerDeterminedNotification winners = new WinnerDeterminedNotification(winnersAIDS);
						AgentHelper.sendSimpleMessage(simulationAgent, environmentAID, ACLMessage.INFORM, winners);
					}
				})
				.then(this.getDistributePotToWinnersBehaviour());
		
		
		mainTask = mainTask.then(new OneShotBehaviour() {
			@Override
			public void action() {
				simulationAgent.resetWinners();
			}
		});
		
		this.setBehaviour(mainTask);
		super.onStart();
	}
	
	public int onEnd(){
		return GameEvent.NEW_HAND.ordinal();
	}
	
	public TransactionBehaviour getDistributePotToWinnersBehaviour(){
		ArrayList<AID> winnersAIDs = new ArrayList<AID>();
		for(AID playerAID : simulationAgent.getWinners().keySet()){
			winnersAIDs.add(playerAID);
		}
		
		DistributePotToWinnersRequest distributePotToWinnersRequest = new DistributePotToWinnersRequest(winnersAIDs);
		return new TransactionBehaviour(simulationAgent, distributePotToWinnersRequest, betmanagerAID);
	}
	
	public TransactionBehaviour getEmptyCommunityCardsBehaviour(){
		EmptyCardsRequest emptyCommunityCardsRequest = new EmptyCardsRequest();
		TransactionBehaviour transactionBehaviour = new TransactionBehaviour(simulationAgent, 
				emptyCommunityCardsRequest, environmentAID);
		return transactionBehaviour;
	}
	
	public TransactionBehaviour getEmptyPotBehaviour(){
		EmptyPotRequest emptyPotRequest = new EmptyPotRequest();
		TransactionBehaviour transactionBehaviour = new TransactionBehaviour(simulationAgent, 
				emptyPotRequest, environmentAID);
		return transactionBehaviour;
	}
}
