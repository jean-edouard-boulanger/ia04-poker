package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import poker.token.model.TokenSet;
import poker.token.model.TokenValueDefinition;
import sma.agent.SimulationAgent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBehaviour;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBehaviour;
import sma.message.Message;
import sma.message.blind.request.ResetBlindRequest;
import sma.message.environment.request.GiveTokenSetToPlayerRequest;
import sma.message.environment.request.SetTokenValueDefinitionRequest;
import sma.agent.helper.experimental.Task.Parallel;;
/**
 * Start a new game, give every players a token set, reset blinds.
 */
public class InitGameBehaviour extends TaskRunnerBehaviour 
{
	private AID environment;
	private AID blindManager;
	private SimulationAgent simulationAgent;
	
	/**
	 * Build an new instance of game initialization behavior.
	 * This behavior do several tasks:
	 *  - set token value definition
	 *  - chip distribution to players
	 *  - blind definition
	 * @param simulationAgent	Agent owning the behavior
	 */
	public InitGameBehaviour(SimulationAgent simulationAgent) {
		super(simulationAgent);

		this.environment = DFServiceHelper.searchService(simulationAgent, "PokerEnvironment", "Environment");
		this.blindManager = DFServiceHelper.searchService(simulationAgent, "BlindManagementAgent","BlindManager");
		this.simulationAgent = simulationAgent;
	}

	@Override
	public void onStart() {

		Behaviour setTokenValue = setTokenDefinitionBehaviour(simulationAgent.getDefaultTokenValueDefinition());
		Behaviour resetBlind = resetBlindBehaviour(simulationAgent.getBlindIncreaseDelayS(), simulationAgent.getDefaultTokenValueDefinition());
		
		// we start the task by setting the token value definition:
		Parallel par = Task.New(setTokenValue).parallel();
				
		// then we give each player their initial token set (in parallel)
		for (AID p : simulationAgent.getGame().getPlayersContainer().getPlayersAIDs())
			par.add(giveTokenBehaviour(p, simulationAgent.getDefaultTokenSet()));
		
		// again in parallel, we reset the blind agent.
		this.setBehaviour(par.add(resetBlind).whenAll());
		
		simulationAgent.addBehaviour(new CheckPlayersActionsBehaviour(simulationAgent));
		
		super.onStart();
	}

	private Behaviour giveTokenBehaviour(AID player, TokenSet tokens){
		Message msg = new GiveTokenSetToPlayerRequest(tokens, player);
		TransactionBehaviour transaction = new TransactionBehaviour(myAgent, msg, environment);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"token set given to player " + player.getLocalName() +".",
				"error while giving token set to player " + player.getLocalName() +"."));
		return transaction;
	}

	private Behaviour resetBlindBehaviour(int time, TokenValueDefinition tokenValueDef){
		Message msg = new ResetBlindRequest(time, tokenValueDef);
		TransactionBehaviour transaction = new TransactionBehaviour(myAgent, msg, blindManager);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"blind increase time interval successfully set.",
				"error while setting blind increase time interval"));
		return transaction;
	}

	private Behaviour setTokenDefinitionBehaviour(TokenValueDefinition tvf){
		Message msg = new SetTokenValueDefinitionRequest(tvf);
		TransactionBehaviour transaction = new TransactionBehaviour(myAgent, msg, environment);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"token value definition successfully set.",
				"error while setting token value definition"));
		return transaction;
	}
	
	/** Transition: Return the NEW_HAND transition code.*/
	@Override
	public int onEnd(){
		return SimulationAgent.GameEvent.NEW_HAND.ordinal();
	}
}
