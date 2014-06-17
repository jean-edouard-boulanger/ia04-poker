package sma.agent.simulationAgent;

import poker.game.player.model.PlayerStatus;
import sma.agent.SimulationAgent;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.RequestTransaction;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.bet.request.BetRequest;
import sma.message.bet.request.FoldRequest;
import sma.message.environment.request.CurrentPlayerChangeRequest;
import sma.message.environment.request.PlayerFoldRequest;
import sma.message.simulation.request.PlayRequest;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PlayBehaviour extends Behaviour {
	
	private static final int BET_REQUEST_STATE = 2;
	private static final int PLAYER_FOLDED_STATE = 32;
	private static final int PLAYER_PLACED_BET = 31;
	private static final int END_STATE = 4;
	
	public int step = 0;
	
	SimulationAgent simulationAgent;
	AID playerAID;
	
	AID environmentAID;
	AID betManagerAID;
	
	RequestTransaction betManagerTransaction;
	RequestTransaction envTransaction;
	
	boolean isError = false;
	
	String betErrorMessage = null;
	boolean isBetError = false;
	
	public PlayBehaviour(SimulationAgent simulationAgent, AID PlayerAID) {
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
		this.playerAID = PlayerAID;
		
		this.environmentAID = DFServiceHelper.searchService(simulationAgent, "PokerEnvironment", "Environment");
		this.betManagerAID = DFServiceHelper.searchService(simulationAgent, "BetManagerAgent","BetManager");
	}

	@Override
	public void action() {
		
		boolean received = false;
		
		if(step == 0){
			// We request the environment to notify the player AID plays
			this.envTransaction = new RequestTransaction(this, new CurrentPlayerChangeRequest(this.playerAID), environmentAID);
			this.envTransaction.sendRequest();
			
			this.step++;
		}
		else if(step == 1){

			// We wait for the environment confirmation
			received = this.envTransaction.checkReply(new EnvironmentMessageVisitor());
			if(received){
				this.step++;
			}
			else {
				block();
			}
		}
		else if(step == 2){

			// We notify that the player AID (and only the player AID) can play (Handled by CheckPlayerActionsBehaviour)
			this.simulationAgent.setPlayerAllowedToBetAID(this.playerAID);
			
			// Now we request the player AID to play. If there was an error during the previous bet, we send the error message as well
			PlayRequest request = new PlayRequest();
			if(this.isBetError){
				request.setRequestResentFollowedToError(true);
				request.setErrorMessage(this.betErrorMessage);
			}
			
			this.envTransaction = new RequestTransaction(this, request, this.playerAID);
			
			// The request is sent
			this.envTransaction.sendRequest();
			this.step++;
		}
		else if(step == 3){

			/* The player action is received
			 * - Fold will lead to step 32
			 * - Bet will lead to step 31 (The request to the betManager is created by the messageVisitor)
			 */
		
			// Not an answer, but simulation receives a new bet request from the player
			//received = this.envTransaction.checkReply(new PlayerMessageVisitor());
			
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchSender(this.playerAID), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			received = AgentHelper.receiveMessage(simulationAgent, mt, new PlayerMessageVisitor());
			
			if(!received){
				block();
			}
		}
		else if(step == 31){

			/* Check the betManager response
			 * - A failure message will lead to step 2 (Bet request)
			 * - A OK message will lead to step 4 (End process)
			 */
			received = this.betManagerTransaction.checkReply(new BetManagerMessageVisitor());
			if(!received){
				block();
			}
		}
		else if(step == 32){

			/*
			 * Notifies the environment that the player folded
			 */
			envTransaction = new RequestTransaction(this, new PlayerFoldRequest(playerAID), environmentAID);
			step = 33;
		}
		else if(step == 33){

			/*
			 * Received the environment answer
			 */
			received = this.envTransaction.checkReply(new EnvironmentMessageVisitor());
			if(!received){
				block();
			}
			else {
				/*
				 * Once received, update local model, and go to the end state
				 */
				this.simulationAgent.getGame().getPlayersContainer().getPlayerByAID(this.playerAID).setStatus(PlayerStatus.FOLDED);
				this.step = PlayBehaviour.END_STATE;
			}
		}
		else {

			/*
			 * We are done, the behaviour will terminate
			 */
			this.step = 4;
		}
	}

	@Override
	public boolean done() {
		return step == 4;
	}
	
	private class EnvironmentMessageVisitor extends MessageVisitor{
		@Override
		public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
			isError = true;
						
			return true;
		}
		@Override
		public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
			return true;
		}
	}
	
	private class PlayerMessageVisitor extends MessageVisitor{
		@Override
		public boolean onBetRequest(BetRequest request, ACLMessage aclMsg) {
			
			System.out.println("DEBUG [PlayBehaviour:" + step + "] Bet message received FROM " + playerAID.getLocalName());
			
			//After the player played, it can't play any longer
			simulationAgent.setPlayerAllowedToBetAID(null);
			
			// Create a request to check if the bet is OK
			betManagerTransaction = new RequestTransaction(PlayBehaviour.this, request, betManagerAID);
			betManagerTransaction.sendRequest();
			
			step = PlayBehaviour.PLAYER_PLACED_BET;
			
			return true;
		}
		
		@Override
		public boolean onFoldRequest(FoldRequest request, ACLMessage aclMsg){

			System.out.println("DEBUG [PlayBehaviour:" + step + "] Fold message received FROM " + playerAID.getLocalName());
			
			//After the player played, it can't play any longer
			simulationAgent.setPlayerAllowedToBetAID(null);
			
			step = PlayBehaviour.PLAYER_FOLDED_STATE;
			
			return true;
		}
		
	}
	
	private class BetManagerMessageVisitor extends MessageVisitor{
		@Override
		public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
			isBetError = true;
			betErrorMessage = msg.getMessage();
			
			System.err.println("ERROR [Simulation.PlayBehaviour:"+ step +"] " + msg.getMessage());
			
			step = PlayBehaviour.BET_REQUEST_STATE;
			
			return true;
		}
		@Override
		public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
			step = PlayBehaviour.END_STATE;
			return true;
		}
	}
	
}
