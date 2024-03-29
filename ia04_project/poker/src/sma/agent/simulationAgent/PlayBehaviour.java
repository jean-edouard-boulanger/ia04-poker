package sma.agent.simulationAgent;

import com.sun.media.jfxmedia.events.PlayerStateEvent.PlayerState;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import poker.game.player.model.PlayerStatus;
import sma.agent.SimulationAgent;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.RequestTransaction;
import sma.message.BooleanMessage;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.bet.request.BetRequest;
import sma.message.bet.request.DoesPlayerHaveToBetRequest;
import sma.message.bet.request.FoldRequest;
import sma.message.environment.request.ChangePlayerStatusRequest;
import sma.message.environment.request.CurrentPlayerChangeRequest;
import sma.message.simulation.request.PlayRequest;

public class PlayBehaviour extends Behaviour {
	
	private static final int BET_REQUEST_STATE = 4;
	private static final int PLAYER_FOLDED_STATE = 52;
	private static final int PLAYER_PLACED_BET = 51;
	private static final int END_STATE = 6;
	
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
		
		/*if(simulationAgent.arePlayRequestsCancelled()){
			this.step = PlayBehaviour.END_STATE;
		}
		else */if(step == 0){
			
			//System.err.println("DEBUG ["+ simulationAgent.getLocalName() +":PlayBehaviour] " + step);
			
			// We first ask the betManager if the player actually have to play
			this.betManagerTransaction = new RequestTransaction(this, new DoesPlayerHaveToBetRequest(this.playerAID), betManagerAID);
			this.betManagerTransaction.sendRequest();
			this.step++;
		}
		else if(step == 1){
			
			//System.err.println("DEBUG ["+ simulationAgent.getLocalName() +":PlayBehaviour] " + step);

			
			received = this.betManagerTransaction.checkReply(new MessageVisitor(){
				
				@Override
				public boolean onBooleanMessage(BooleanMessage message, ACLMessage aclMsg) {
					if(message.getValue()){
						// If the player has to pay, we go to next step
						step = 2;
					}
					else {
						//Otherwise, we leave the behaviour, and notify that the following players won't play as well
						//simulationAgent.cancelNextPlayRequests();
						step = PlayBehaviour.END_STATE;
					}
					return true;
				}
			});
			
			if(!received){
				block();
			}
			
		}
		else if(step == 2){
			
			//System.err.println("DEBUG ["+ simulationAgent.getLocalName() +":PlayBehaviour] " + step);

			
			// We request the environment to notify that the player AID plays
			this.envTransaction = new RequestTransaction(this, new CurrentPlayerChangeRequest(this.playerAID), environmentAID);
			this.envTransaction.sendRequest();
			
			this.step++;
		}
		else if(step == 3){
			
			//System.err.println("DEBUG ["+ simulationAgent.getLocalName() +":PlayBehaviour] " + step);

			// We wait for the environment confirmation
			received = this.envTransaction.checkReply(new EnvironmentMessageVisitor());
			if(received){
				this.step++;
			}
			else {
				block();
			}
		}
		else if(step == 4){
			
			//System.err.println("DEBUG ["+ simulationAgent.getLocalName() +":PlayBehaviour] " + step);
			
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
		else if(step == 5){

			//System.err.println("DEBUG ["+ simulationAgent.getLocalName() +":PlayBehaviour] " + step);
			
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
		else if(step == 51){

			//System.err.println("DEBUG ["+ simulationAgent.getLocalName() +":PlayBehaviour] " + step);
			
			/* Check the betManager response
			 * - A failure message will lead to step 2 (Bet request)
			 * - A OK message will lead to step 4 (End process)
			 */
			received = this.betManagerTransaction.checkReply(new BetManagerMessageVisitor());
			if(!received){
				block();
			}
		}
		else if(step == 52){

			//System.err.println("DEBUG ["+ simulationAgent.getLocalName() +":PlayBehaviour] " + step);
			
			/*
			 * Notifies the environment that the player folded
			 */
			
			// Now use ChangePlayerStatusRequest instead of PlayerFoldRequest
			// envTransaction = new RequestTransaction(this, new PlayerFoldRequest(playerAID), environmentAID);
			
			this.simulationAgent.getGame().getPlayersContainer().getPlayerByAID(this.playerAID);
			this.envTransaction = new RequestTransaction(this, new ChangePlayerStatusRequest(this.playerAID, PlayerStatus.FOLDED), this.environmentAID);
			this.envTransaction.sendRequest();
			
			step = 53;
		}
		else if(step == 53){

			//System.err.println("DEBUG ["+ simulationAgent.getLocalName() +":PlayBehaviour] " + step);
			
			/*
			 * Received the environment answer
			 */
			received = this.envTransaction.checkReply(new EnvironmentMessageVisitor());
			if(!received){
				block();
			}
			else 
			{
				/*
				 * Once received, update local model, and jump to the end state
				 */
				this.simulationAgent.getGame().getPlayersContainer().getPlayerByAID(this.playerAID).setStatus(PlayerStatus.FOLDED);
				this.step = PlayBehaviour.END_STATE;
			}
		}
		else {

			//System.err.println("DEBUG ["+ simulationAgent.getLocalName() +":PlayBehaviour] " + step);
			
			/*
			 * We are done, the behaviour will terminate
			 */
			this.step = 6;
		}
	}
	
	@Override
	public boolean done() {
		return step == 6;
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
			
			//After the player played, he can't play any longer
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
			
			//After the player played, he can't play any longer
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
