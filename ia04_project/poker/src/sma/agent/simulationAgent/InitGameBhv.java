package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import poker.game.player.model.Player;
import poker.token.model.TokenSet;
import poker.token.model.TokenValueDefinition;
import sma.agent.SimulationAgent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.blind.request.ResetBlindRequest;
import sma.message.environment.request.GiveTokenSetToPlayerRequest;
import sma.message.environment.request.SetTokenValueDefinitionRequest;

/**
 * Start a new game, give every players a token set, reset blinds.
 */
public class InitGameBhv extends ParallelBehaviour  
{
	private AID environment;
	private AID blindManager;

	/**
	 * Build an new instance of game initialization behavior.
	 * This behavior do several tasks (in parallel):
	 *  - chip distribution to players
	 *  - blind definition
	 *  - set token value definition
	 * @param simAgent	Agent owning the behavior
	 */
	public InitGameBhv(SimulationAgent simAgent) {

		super(simAgent, ParallelBehaviour.WHEN_ALL);

		this.environment = DFServiceHelper.searchService(simAgent, "PokerEnvironment", "Environment");
		this.blindManager = DFServiceHelper.searchService(simAgent, "BlindManagementAgent","BlindManager");
				
		// The parallel behavior give every players a tokenSet (based on the default distribution in simAgent).
		for (Player p : simAgent.getGame().getPlayersContainer().getPlayers()){
			this.addSubBehaviour(getTokenDistributionBehaviour(p, simAgent.getDefaultTokenSet()));
		}
		
		// we set token value definition
		this.addSubBehaviour(getSetTokenValueDefinitionBehviour(simAgent.getDefaultTokenValueDefinition()));
		
		// we also reset blinds:
		this.addSubBehaviour(getResetBlindBehviour(simAgent.getBlindIncreaseDelayS(), simAgent.getDefaultTokenValueDefinition()));
		
	}
	
	

	private Behaviour getTokenDistributionBehaviour(Player p, TokenSet tokens){
		Message msg = new GiveTokenSetToPlayerRequest(tokens, p.getAID());
		
		TransactionBhv transaction = new TransactionBhv(myAgent, msg, environment);
		transaction.setResponseVisitor(new MessageVisitor(){
			@Override
			public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
				System.out.println("[" + myAgent.getLocalName() + "] initial token set given to player.");
				return true;
			}
			@Override
			public boolean onFailureMessage(FailureMessage msg,ACLMessage aclMsg) {
				System.out.println("[" + myAgent.getLocalName() + "] error while giving initial token set to player: " + msg.getMessage());
				return true;
			}
		});		
		
		return transaction;
	}
	
	private Behaviour getResetBlindBehviour(int time, TokenValueDefinition tokenValueDef){
		Message msg = new ResetBlindRequest(time, tokenValueDef);
		
		TransactionBhv transaction = new TransactionBhv(myAgent, msg, blindManager);
		transaction.setResponseVisitor(new MessageVisitor(){
			@Override
			public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
				System.out.println("[" + myAgent.getLocalName() + "] blind increase time interval succesfully set.");
				return true;
			}
			@Override
			public boolean onFailureMessage(FailureMessage msg,ACLMessage aclMsg) {
				System.out.println("[" + myAgent.getLocalName() + "] error while setting blind increase time interval: " + msg.getMessage());
				return true;
			}
		});	
		
		return transaction;
	}
	
	private Behaviour getSetTokenValueDefinitionBehviour(TokenValueDefinition tvf){
		Message msg = new SetTokenValueDefinitionRequest(tvf);
	
		TransactionBhv transaction = new TransactionBhv(myAgent, msg, environment);
		transaction.setResponseVisitor(new MessageVisitor(){
			@Override
			public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
				System.out.println("[" + myAgent.getLocalName() + "] token value definition succesfully set.");
				return true;
			}
			@Override
			public boolean onFailureMessage(FailureMessage msg,ACLMessage aclMsg) {
				System.out.println("[" + myAgent.getLocalName() + "] error while setting token value definition: " + msg.getMessage());
				return true;
			}
		});	
		
		return transaction;
	}
	
	/**
	 * Transition: Return the NEW_HAND transition code.
	 */
	@Override
	public int onEnd(){
		return SimulationAgent.GameEvent.NEW_HAND.ordinal();
		
	}
}
