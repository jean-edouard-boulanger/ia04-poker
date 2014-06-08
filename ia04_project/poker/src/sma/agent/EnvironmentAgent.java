package sma.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.ArrayList;

import poker.card.exception.CommunityCardsFullException;
import poker.card.exception.UserDeckFullException;
import poker.card.model.Card;
import poker.card.model.CommunityCards;
import poker.game.exception.NotRegisteredPlayerException;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.model.BlindValueDefinition;
import poker.game.model.Game;
import poker.game.player.model.Player;
import poker.token.model.TokenValueDefinition;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import sma.message.environment.notification.CardAddedToCommunityCardsNotification;
import sma.message.environment.notification.PlayerReceivedCardNotification;
import sma.message.environment.request.AddCommunityCardRequest;
import sma.message.environment.request.CurrentPlayerChangeRequest;
import sma.message.environment.request.DealCardToPlayerRequest;
import sma.agent.simulationAgent.PlayerSubscriptionBhv;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.PlayerSubscriptionRequest;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.request.AddPlayerTableRequest;

public class EnvironmentAgent extends Agent {
	
	private Game game;
	
	private EnvironmentMessageVisitor msgVisitor;
	
	public EnvironmentAgent(){
		this.game = new Game();
		this.msgVisitor = new EnvironmentMessageVisitor();
	}
	
	@Override
	public void setup()
	{
		super.setup();
		DFServiceHelper.registerService(this, "PokerEnvironment","Environment");
	}
	
	private class EnvironmentReceiveRequestBehaviour extends Behaviour{
		
		MessageTemplate receiveRequestMessageTemplate;
		
		public EnvironmentReceiveRequestBehaviour(){
			super();
			this.receiveRequestMessageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		}
		
		@Override
		public void action() {
			if(!AgentHelper.receiveMessage(this.myAgent, receiveRequestMessageTemplate, msgVisitor)){
				block();
			}
		}

		@Override
		public boolean done() {
			return false;
		}
	}
	
	private class EnvironmentMessageVisitor extends MessageVisitor{
		
		public boolean onAddCommunityCardRequest(AddCommunityCardRequest request, ACLMessage aclMsg) {
			Card newCommunityCard = request.getNewCard();
			
			try {
				game.getCommunityCards().pushCard(newCommunityCard);
			} catch (CommunityCardsFullException e) {
				//CommunityCards is full
				e.printStackTrace();
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.FAILURE, new FailureMessage(e.getMessage()));
				return true;
			}
			
			//Card successfully added
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, game.getPlayersAIDs(), ACLMessage.INFORM, new CardAddedToCommunityCardsNotification(newCommunityCard));
			
			return true;
		}

		public boolean onAddPlayerTableRequest(AddPlayerTableRequest request, ACLMessage aclMsg) {
			
			try{
				game.addPlayer(request.getNewPlayer());
			}
			catch(PlayerAlreadyRegisteredException ex){
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.FAILURE, new FailureMessage(ex.getMessage()));
				return true;
			}
			
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, game.getPlayersAIDs(), ACLMessage.INFORM, new PlayerSitOnTableNotification(request.getNewPlayer()));
			AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage());
			
			return true;
		}
		
		@Override
		public boolean onDealCardToPlayerRequest(DealCardToPlayerRequest request, ACLMessage aclMsg){
			
			try{
				game.getPlayerByAID(request.getPlayerAID()).getDeck().addCard(request.getDealtCard());
			}
			catch(UserDeckFullException ex){
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.FAILURE, new FailureMessage(ex.getMessage()));
				return true;
			}
			
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, request.getPlayerAID(), ACLMessage.INFORM, new PlayerReceivedCardNotification(request.getPlayerAID(), request.getDealtCard()));
			AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage());

			return true;
		}
		
		@Override
		public boolean onCurrentPlayerChangeRequest(CurrentPlayerChangeRequest request, ACLMessage aclMsg){
			
			try{
				game.setCurrentPlayer(game.getPlayerByAID(request.getPlayerAID()));
			}
			catch(NotRegisteredPlayerException ex){
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.INFORM, new FailureMessage(ex.getMessage()));
				return true;
			}
			
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, game.getPlayersAIDs(), ACLMessage.INFORM, new CurrentPlayerChangeRequest(request.getPlayerAID()));
			
			return true;
		}
		
		
		
	}
}
