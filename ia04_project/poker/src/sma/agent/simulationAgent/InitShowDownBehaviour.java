package sma.agent.simulationAgent;

import poker.game.player.model.Player;
import jade.core.AID;
import jade.core.Agent;
import sma.agent.SimulationAgent;
import sma.agent.SimulationAgent.GameEvent;
import sma.agent.helper.BlankBehaviour;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBehaviour;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBehaviour;
import sma.message.environment.request.RevealPlayerCardsRequest;

public class InitShowDownBehaviour extends TaskRunnerBehaviour {

	SimulationAgent simulationAgent;
	
	AID environmentAID;
		
	public InitShowDownBehaviour(SimulationAgent simulationAgent) {
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
		
		this.environmentAID = DFServiceHelper.searchService(simulationAgent, "PokerEnvironment", "Environment");
	}
	
	@Override
	public void onStart() {
		
		Task mainTask = Task.New(new BlankBehaviour());
		
		for(Player p : this.simulationAgent.getGame().getPlayersContainer().getPlayersInGame()){
			mainTask = mainTask.then(getRequestPlayerRevealCardsBehaviour(p.getAID()));
		}
		
		this.setBehaviour(mainTask);
		super.onStart();
	}

	@Override
	public int onEnd(){
		return GameEvent.FIND_HAND_WINNERS.ordinal();
	}
	
	private TransactionBehaviour getRequestPlayerRevealCardsBehaviour(AID playerAID){
		RevealPlayerCardsRequest revealPlayerCardsRequest = new RevealPlayerCardsRequest(playerAID);
		TransactionBehaviour transactionBehaviour = new TransactionBehaviour(simulationAgent, revealPlayerCardsRequest, this.environmentAID);
		transactionBehaviour.setResponseVisitor(new SimpleVisitor(simulationAgent, 
				"Cards from player " + playerAID.getLocalName() + " revealed successfully", 
				"Could not reveal " + playerAID.getLocalName() + " player's cards"));
		return transactionBehaviour;
	}
	
}
