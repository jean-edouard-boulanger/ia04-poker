package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import poker.game.model.HandStep;
import sma.agent.SimulationAgent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.dealer.request.DealRequest;
import sma.message.environment.request.EmptyCommunityCardsRequest;

/**
 * The behaviour handle the intialization of a new game:
 * - community cards are removed from the table.
 * - player card are dealt
 * (those tasks are done in parallel).
 */
public class InitHandBhv extends ParallelBehaviour {

	private AID environment;
	private AID dealer;

	public InitHandBhv(SimulationAgent simAgent) {
		super(simAgent, ParallelBehaviour.WHEN_ALL);
		this.environment = DFServiceHelper.searchService(simAgent, "PokerEnvironment", "Environment");
		this.dealer = DFServiceHelper.searchService(simAgent, "DealerAgent","Dealer");
		
		this.addSubBehaviour(getCommunityCardResetBehavior());
		this.addSubBehaviour(getCardDistributionBehavior());
	}
	
	private Behaviour getCardDistributionBehavior() {
		Message msg = new DealRequest(HandStep.PLAYER_CARDS_DEAL);
		
		TransactionBhv transaction = new TransactionBhv(myAgent, msg, dealer);
		transaction.setResponseVisitor(new MessageVisitor(){
			@Override
			public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
				System.out.println("[" + myAgent.getLocalName() + "] cards dealt successfully.");
				return true;
			}
			@Override
			public boolean onFailureMessage(FailureMessage msg,ACLMessage aclMsg) {
				System.out.println("[" + myAgent.getLocalName() + "] error dealing cards: " + msg.getMessage());
				return true;
			}
		});	
		
		return transaction;
	}

	private Behaviour getCommunityCardResetBehavior() {
		Message msg = new EmptyCommunityCardsRequest();
		
		TransactionBhv transaction = new TransactionBhv(myAgent, msg, environment);
		transaction.setResponseVisitor(new MessageVisitor(){
			@Override
			public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
				System.out.println("[" + myAgent.getLocalName() + "] community cards emptied successfully.");
				return true;
			}
			@Override
			public boolean onFailureMessage(FailureMessage msg,ACLMessage aclMsg) {
				System.out.println("[" + myAgent.getLocalName() + "] error while cleaning community cards: " + msg.getMessage());
				return true;
			}
		});	
		
		return transaction;
	}

	/**
	 * Transition: return the NEW_ROUND transition code.
	 */
	@Override
	public int onEnd(){
		return SimulationAgent.GameEvent.NEW_ROUND.ordinal();
		
	}

}
