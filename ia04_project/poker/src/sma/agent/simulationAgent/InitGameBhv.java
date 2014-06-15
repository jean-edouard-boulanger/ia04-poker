package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import poker.token.model.TokenSet;
import poker.token.model.TokenValueDefinition;
import sma.agent.SimulationAgent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBhv;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.Task.Parallel;
import sma.agent.helper.experimental.TaskRunnerBhv;
import sma.message.Message;
import sma.message.blind.request.ResetBlindRequest;
import sma.message.environment.request.GiveTokenSetToPlayerRequest;
import sma.message.environment.request.SetTokenValueDefinitionRequest;

/**
 * Start a new game, give every players a token set, reset blinds.
 */
public class InitGameBhv extends TaskRunnerBhv  
{
    private AID environment;
    private AID blindManager;

    /**
     * Build an new instance of game initialization behavior.
     * This behavior do several tasks:
     *  - set token value definition
     *  - chip distribution to players
     *  - blind definition
     * @param simAgent	Agent owning the behavior
     */
    public InitGameBhv(SimulationAgent simAgent) {
	super();

	this.environment = DFServiceHelper.searchService(simAgent, "PokerEnvironment", "Environment");
	this.blindManager = DFServiceHelper.searchService(simAgent, "BlindManagementAgent","BlindManager");

	Behaviour setTokenValue = setTokenDefinitionBehaviour(simAgent.getDefaultTokenValueDefinition());
	Behaviour resetBlind = resetBlindBehaviour(simAgent.getBlindIncreaseDelayS(), simAgent.getDefaultTokenValueDefinition());

	// we start the task by setting the token value definition:
	Parallel par = Task.New(setTokenValue).parallel();
	
	// then we give each player their initial token set (in parallel)
	for (AID p : simAgent.getGame().getPlayersContainer().getPlayersAIDs())
	    par.add(giveTokenBehaviour(p, simAgent.getDefaultTokenSet()));
	
	// again in parallel, we reset the blind agent.
	this.setBehaviour(par.add(resetBlind).whenAll());
    }

    private Behaviour giveTokenBehaviour(AID player, TokenSet tokens){
	Message msg = new GiveTokenSetToPlayerRequest(tokens, player);
	TransactionBhv transaction = new TransactionBhv(myAgent, msg, environment);
	transaction.setResponseVisitor(new SimpleVisitor(myAgent,
		"token set given to player.",
		"error while giving token set to player."));
	return transaction;
    }

    private Behaviour resetBlindBehaviour(int time, TokenValueDefinition tokenValueDef){
	Message msg = new ResetBlindRequest(time, tokenValueDef);
	TransactionBhv transaction = new TransactionBhv(myAgent, msg, blindManager);
	transaction.setResponseVisitor(new SimpleVisitor(myAgent,
		"blind increase time interval successfully set.",
		"error while setting blind increase time interval"));
	return transaction;
    }

    private Behaviour setTokenDefinitionBehaviour(TokenValueDefinition tvf){
	Message msg = new SetTokenValueDefinitionRequest(tvf);
	TransactionBhv transaction = new TransactionBhv(myAgent, msg, environment);
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
