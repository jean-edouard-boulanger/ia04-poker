package sma.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

import poker.game.model.AIPlayerType;
import poker.game.model.Game;
import sma.agent.aiplayeragent.messagevisitor.AIPlayerAgentMessageVisitor;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBehaviour;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.PlayerSubscriptionRequest;
import sma.message.bet.request.BetRequest;
import sma.message.bet.request.FoldRequest;

public class AIPlayerAgent extends Agent {

	private Game game;

	private AIPlayerAgentMessageVisitor msgVisitor;
	private AIPlayerFailureMessageVisitor msgVisitor_failure;

	private ACLMessage playRequestMessage;
	
	private AIPlayerType playerType;
	
	@Override
	public void setup()
	{
		super.setup();

		game = new Game();

		Random r = new Random();
		
		playerType = AIPlayerType.values()[r.nextInt(AIPlayerType.values().length)];
		
		this.msgVisitor = new AIPlayerAgentMessageVisitor(this, playerType);

		this.msgVisitor_failure = new AIPlayerFailureMessageVisitor();

		addBehaviour(new AIPlayerReceiveNotificationBehaviour(this));
		addBehaviour(new AIPlayerReceiveRequestBehaviour(this));
		addBehaviour(new AIPlayerReceiveFailureBehaviour(this));
		
		//Subscribing to environment
		AID simulation = DFServiceHelper.searchService(this, "PokerSimulation","Simulation");
		addBehaviour(new TransactionBehaviour(this, new PlayerSubscriptionRequest(getLocalName()), simulation, ACLMessage.SUBSCRIBE));
	}

	/**************************************
	 *  Listening notifications
	 */
	private class AIPlayerReceiveNotificationBehaviour extends CyclicBehaviour{

		MessageTemplate receiveNotificationMessageTemplate;

		public AIPlayerReceiveNotificationBehaviour(Agent agent){
			super(agent);
			this.receiveNotificationMessageTemplate = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
		}

		@Override
		public void action() {
			if(!AgentHelper.receiveMessage(this.myAgent, receiveNotificationMessageTemplate, msgVisitor)){
				block();
			}
		}
	}

	/**************************************
	 *  Listening request
	 */
	private class AIPlayerReceiveRequestBehaviour extends CyclicBehaviour{

		MessageTemplate receiveRequestMessageTemplate;

		public AIPlayerReceiveRequestBehaviour(Agent agent){
			super(agent);
			this.receiveRequestMessageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		}

		@Override
		public void action() {
			if(!AgentHelper.receiveMessage(this.myAgent, receiveRequestMessageTemplate, msgVisitor)){
				block();
			}
		}
	}
	
	/**************************************
	 *  Listening failure
	 */
	private class AIPlayerReceiveFailureBehaviour extends CyclicBehaviour{

		MessageTemplate receiveFailureMessageTemplate;

		public AIPlayerReceiveFailureBehaviour(Agent agent){
			super(agent);
			this.receiveFailureMessageTemplate = MessageTemplate.MatchPerformative(ACLMessage.FAILURE);
		}

		@Override
		public void action() {
			if(!AgentHelper.receiveMessage(this.myAgent, receiveFailureMessageTemplate, msgVisitor_failure)){
				block();
			}
		}
	}
	
	/**************************************
	 *  Failure message visitor
	 */
	private class AIPlayerFailureMessageVisitor extends MessageVisitor {

		@Override
		public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {

			System.out.println("[" + getLocalName() + "] Received falure message with content: '" + msg.getMessage() + "'.");
			
			return true;
		}
	}

	/**************************************
	 *  Private functions related to IHM events
	 */

	public void replyBetToSimulationPlayRequest(int betAmount) {
		if(playRequestMessage != null) {
			System.out.println("[AIPlayer] Player " + game.getPlayersContainer().getPlayerByAID(getAID()).getNickname() + " wants to bet: " + betAmount);
			
			//Answering to simulation play request
			AgentHelper.sendReply(this, playRequestMessage, ACLMessage.REQUEST, new BetRequest(betAmount, getAID()));
			
			//Setting play request to null, waiting for a new request from simulation
			playRequestMessage = null;
		}
	}
	
	public void replyFoldToSimulationPlayRequest() {
		if(playRequestMessage != null) {
			System.out.println("[AIPlayer] Player " + game.getPlayersContainer().getPlayerByAID(getAID()).getNickname() + " wants to fold.");	
			
			//Answering to simulation play request
			AgentHelper.sendReply(this, playRequestMessage, ACLMessage.REQUEST, new FoldRequest());
			
			//Setting play request to null, waiting for a new request from simulation
			playRequestMessage = null;
		}
	}
	
	public ACLMessage getPlayRequestMessage() {
		return playRequestMessage;
	}

	public void setPlayRequestMessage(ACLMessage playRequestMessage) {
		this.playRequestMessage = playRequestMessage;
	}

	public Game getGame() {
		return this.game;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public AIPlayerType getPlayerType() {
		return playerType;
	}

	public void setPlayerType(AIPlayerType playerType) {
		this.playerType = playerType;
	}
}
