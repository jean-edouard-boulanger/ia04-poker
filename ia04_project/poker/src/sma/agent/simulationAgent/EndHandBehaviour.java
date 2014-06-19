package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.Agent;
import sma.agent.SimulationAgent;
import sma.agent.SimulationAgent.GameEvent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBehaviour;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBehaviour;
import sma.message.environment.request.EmptyCommunityCardsRequest;

public class EndHandBehaviour extends TaskRunnerBehaviour {

	private SimulationAgent simulationAgent;
	private AID environment;
	
	public EndHandBehaviour(SimulationAgent simulationAgent) {
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
		this.environment = DFServiceHelper.searchService(simulationAgent, "PokerEnvironment", "Environment");
	}
	
	public void onStart(){
		
		Task mainTask = Task.New(this.getEmptyCommunityCardsBehaviour())
				.then(this.getEmptyPotBehaviour());
		
		this.setBehaviour(mainTask);
		super.onStart();
	}
	
	public int onEnd(){
		return GameEvent.NEW_HAND.ordinal();
	}
	
	public TransactionBehaviour getEmptyCommunityCardsBehaviour(){
		EmptyCommunityCardsRequest emptyCommunityCardsRequest = new EmptyCommunityCardsRequest();
		TransactionBehaviour transactionBehaviour = new TransactionBehaviour(simulationAgent, 
				emptyCommunityCardsRequest, environment);
		return transactionBehaviour;
	}
	
	public TransactionBehaviour getEmptyPotBehaviour(){
		TransactionBehaviour transactionBehaviour = new TransactionBehaviour(simulationAgent, 
				emptyCommunityCardsRequest, environment);
		return transactionBehaviour;
	}
	
}
