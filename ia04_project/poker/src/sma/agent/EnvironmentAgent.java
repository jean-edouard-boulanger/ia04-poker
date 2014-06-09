package sma.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

import poker.card.exception.CommunityCardsFullException;
import poker.card.exception.UserDeckFullException;
import poker.card.model.Card;
import poker.game.exception.NotRegisteredPlayerException;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.model.Game;
import poker.token.exception.InvalidTokenAmountException;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.NotificationSubscriber;
import sma.message.OKMessage;
import sma.message.SubscriptionOKMessage;
import sma.message.environment.notification.BlindValueDefinitionChangedNotification;
import sma.message.environment.notification.CardAddedToCommunityCardsNotification;
import sma.message.environment.notification.CommunityCardsEmptiedNotification;
import sma.message.environment.notification.PlayerReceivedCardNotification;
import sma.message.environment.notification.PlayerReceivedTokenSetNotification;
import sma.message.environment.notification.PlayerReceivedUnknownCardNotification;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.request.AddCommunityCardRequest;
import sma.message.environment.request.AddPlayerTableRequest;
import sma.message.environment.request.BlindValueDefinitionChangeRequest;
import sma.message.environment.request.CurrentPlayerChangeRequest;
import sma.message.environment.request.DealCardToPlayerRequest;
import sma.message.environment.request.EmptyCommunityCardsRequest;
import sma.message.environment.request.GiveTokenSetToPlayerRequest;

public class EnvironmentAgent extends Agent {
	
	private Game game;
	
	private ArrayList<AID> subscribers;
	private EnvironmentMessageVisitor msgVisitor;
	private NotificationSubscriber notificationSubscriber;
	
	public EnvironmentAgent(){
		this.game = new Game();
		this.msgVisitor = new EnvironmentMessageVisitor();
		this.notificationSubscriber = new NotificationSubscriber();
		this.subscribers = new ArrayList<>();
	}
	
	@Override
	public void setup()
	{
		super.setup();
		DFServiceHelper.registerService(this, "PokerEnvironment","Environment");
		this.addBehaviour(new AddSubscriberBehaviour(this));
		this.addBehaviour(new EnvironmentReceiveRequestBehaviour(this));
	}
	
	private class EnvironmentReceiveRequestBehaviour extends CyclicBehaviour{
		
		MessageTemplate receiveRequestMessageTemplate;
		
		public EnvironmentReceiveRequestBehaviour(Agent agent){
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
	
	private class AddSubscriberBehaviour extends CyclicBehaviour {
		MessageTemplate receiveSubscriptionRequestMessageTemplate;
		private EnvironmentAgent environment;
		
		public AddSubscriberBehaviour(EnvironmentAgent agent){
			super(agent);
			this.environment = agent;
			this.receiveSubscriptionRequestMessageTemplate = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
		}
		
		@Override
		public void action() {
			
			ACLMessage msg = myAgent.receive(receiveSubscriptionRequestMessageTemplate);
			
			if(msg == null) {
				block();
			}
			else {
				AID senderAID = msg.getSender();
				if(!subscribers.contains(senderAID)) {
					subscribers.add(senderAID);
					AgentHelper.sendReply(myAgent, msg, ACLMessage.INFORM, new SubscriptionOKMessage(environment.game));
				}
				else {
					AgentHelper.sendReply(myAgent, msg, ACLMessage.FAILURE, new FailureMessage("Already subscribed!"));
				}
			}
		}
	}
	
	private class EnvironmentMessageVisitor extends MessageVisitor{
	
		public boolean onAddPlayerTableRequest(AddPlayerTableRequest request, ACLMessage aclMsg) {
			
			try{
				game.addPlayer(request.getNewPlayer());
			}
			catch(PlayerAlreadyRegisteredException ex){
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.FAILURE, new FailureMessage(ex.getMessage()));
				return true;
			}
			
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, subscribers, ACLMessage.PROPAGATE, new PlayerSitOnTableNotification(request.getNewPlayer()));
			AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage());
			
			return true;
		}
		
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
			
			CardAddedToCommunityCardsNotification notification = new CardAddedToCommunityCardsNotification(newCommunityCard);
			
			//Card successfully added
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, subscribers, ACLMessage.PROPAGATE, notification);
			
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
			
			PlayerReceivedCardNotification notification = new PlayerReceivedCardNotification(request.getPlayerAID(), request.getDealtCard());
			
			//Informing player who received the card
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, request.getPlayerAID(), ACLMessage.PROPAGATE, notification);
			
			//Informing other players that he received a card (unknown for them)
			ArrayList<AID> subscribersToNofitfy = (ArrayList<AID>) subscribers.clone();
			subscribersToNofitfy.remove(request.getPlayerAID());
			
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, subscribersToNofitfy, ACLMessage.PROPAGATE, new PlayerReceivedUnknownCardNotification(request.getPlayerAID()));
			
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
			
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, subscribers, ACLMessage.PROPAGATE, new CurrentPlayerChangeRequest(request.getPlayerAID()));
			
			return true;
		}
		
		@Override
		public boolean onEmptyCommunityCardsRequest(EmptyCommunityCardsRequest request, ACLMessage aclMsg) {
			
			game.getCommunityCards().popCards();
			
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, subscribers, ACLMessage.PROPAGATE, new CommunityCardsEmptiedNotification());
			
			AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage());
			
			return true;
		}
		
		@Override
		public boolean onGiveTokenSetToPlayerRequest(GiveTokenSetToPlayerRequest request, ACLMessage aclMsg) {
			
			try {
				game.getPlayerByAID(request.getPlayerAID()).getTokens().AddTokenSet(request.getTokenSet());
			} catch (InvalidTokenAmountException ex) {
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.INFORM, new FailureMessage(ex.getMessage()));
				return true;
			}
			
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, subscribers, ACLMessage.PROPAGATE, new PlayerReceivedTokenSetNotification(request.getPlayerAID(), request.getTokenSet()));
			
			AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage());
			
			return true;
		}
		
		@Override
		public boolean onBlindValueDefinitionChangeRequest(BlindValueDefinitionChangeRequest request, ACLMessage aclMsg){
			
			game.setBlindValueDefinition(request.getBlindValueDefinition());
			
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, subscribers, ACLMessage.PROPAGATE, new BlindValueDefinitionChangedNotification(request.getBlindValueDefinition()));
	
			AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage());
			
			return true;
		}
		
		
		
		
	}
}
