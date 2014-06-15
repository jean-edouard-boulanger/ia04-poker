package sma.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

import org.omg.CORBA.Request;

import poker.card.exception.CommunityCardsFullException;
import poker.card.exception.UserDeckFullException;
import poker.card.model.Card;
import poker.game.exception.NoPlaceAvailableException;
import poker.game.exception.NotRegisteredPlayerException;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.model.Game;
import poker.game.player.model.Player;
import poker.token.exception.InvalidTokenAmountException;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.SubscriptionOKMessage;
import sma.message.bet.request.BetRequest;
import sma.message.environment.notification.BetNotification;
import sma.message.environment.notification.BlindValueDefinitionChangedNotification;
import sma.message.environment.notification.CardAddedToCommunityCardsNotification;
import sma.message.environment.notification.CommunityCardsEmptiedNotification;
import sma.message.environment.notification.DealerChangedNotification;
import sma.message.environment.notification.PlayerReceivedCardNotification;
import sma.message.environment.notification.PlayerReceivedTokenSetNotification;
import sma.message.environment.notification.PlayerReceivedUnknownCardNotification;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.notification.TokenValueDefinitionChangedNotification;
import sma.message.environment.request.AddCommunityCardRequest;
import sma.message.environment.request.AddPlayerTableRequest;
import sma.message.environment.request.BlindValueDefinitionChangeRequest;
import sma.message.environment.request.CurrentPlayerChangeRequest;
import sma.message.environment.request.DealCardToPlayerRequest;
import sma.message.environment.request.EmptyCommunityCardsRequest;
import sma.message.environment.request.GiveTokenSetToPlayerRequest;
import sma.message.environment.request.SetDealerRequest;
import sma.message.environment.request.SetTokenValueDefinitionRequest;

public class EnvironmentAgent extends Agent {

	private Game game;

	private ArrayList<AID> subscribers;
	private EnvironmentMessageVisitor msgVisitor;

	public EnvironmentAgent(){
		this.game = new Game();
		this.msgVisitor = new EnvironmentMessageVisitor();
		this.subscribers = new ArrayList<AID>();
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

		@Override
		public boolean onAddPlayerTableRequest(AddPlayerTableRequest request, ACLMessage aclMsg) {

			try{
				game.getPlayersContainer().addPlayer(request.getNewPlayer());
				subscribers.add(request.getNewPlayer().getAID());
			}
			catch(PlayerAlreadyRegisteredException e){
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.FAILURE, new FailureMessage(e.getMessage()));
				return true;
			} catch (NoPlaceAvailableException e) {
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.FAILURE, new FailureMessage(e.getMessage()));
				return true;
			}
			// we sent a subscription notification to the player:
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, request.getNewPlayer().getAID(), ACLMessage.PROPAGATE, new SubscriptionOKMessage(game));

			ArrayList<AID> subscribersToNofitfy = new ArrayList<AID>(subscribers);
			subscribersToNofitfy.remove(request.getNewPlayer().getAID());
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, subscribersToNofitfy, ACLMessage.PROPAGATE, new PlayerSitOnTableNotification(request.getNewPlayer()));

			AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage());

			return true;
		}

		@Override
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
				game.getPlayersContainer().getPlayerByAID(request.getPlayerAID()).getDeck().addCard(request.getDealtCard());
			}
			catch(UserDeckFullException ex){
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.FAILURE, new FailureMessage(ex.getMessage()));
				return true;
			}

			//Informing player who received the card
			PlayerReceivedCardNotification notification = new PlayerReceivedCardNotification(request.getPlayerAID(), request.getDealtCard());
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, request.getPlayerAID(), ACLMessage.PROPAGATE, notification);

			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, game.getPlayersContainer().getPlayersAIDs(), ACLMessage.PROPAGATE, new PlayerReceivedUnknownCardNotification(request.getPlayerAID()));

			AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage());
			return true;
		}

		@Override
		public boolean onCurrentPlayerChangeRequest(CurrentPlayerChangeRequest request, ACLMessage aclMsg){
			try{
				game.getPlayersContainer().setCurrentPlayer(game.getPlayersContainer().getPlayerByAID(request.getPlayerAID()));
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
			game.getPlayersContainer().getPlayerByAID(request.getPlayerAID()).getTokens().AddTokenSet(request.getTokenSet());
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

		@Override
		public boolean onSetTokenValueDefinitionRequest(SetTokenValueDefinitionRequest notif, ACLMessage aclMsg) {
			game.getBetContainer().setTokenValueDefinition(notif.getTokenValueDefinition());
			AgentHelper.sendSimpleMessage(EnvironmentAgent.this, subscribers, ACLMessage.PROPAGATE, new TokenValueDefinitionChangedNotification(notif.getTokenValueDefinition()));
			AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage());
			return true;
		}

		@Override
		public boolean onSetDealerRequest(SetDealerRequest request, ACLMessage aclMsg) {
			try {
				game.getPlayersContainer().setDealer(request.getDealer());
				AgentHelper.sendSimpleMessage(EnvironmentAgent.this, subscribers, ACLMessage.PROPAGATE, new DealerChangedNotification(request.getDealer()));
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage());
			} catch (NotRegisteredPlayerException e) {
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.FAILURE, new FailureMessage(e.getMessage()));
			}			
			return true;
		}
		
		@Override
		public boolean onBetRequest(BetRequest request, ACLMessage aclMsg) {
			try {
				Player player = game.getPlayersContainer().getPlayerByAID(request.getPlayerAID());
				player.setTokens(player.getTokens().SubstractTokenSet(request.getTokenSet()));				
				game.getBetContainer().addTokenToPlayerBet(request.getPlayerAID(), request.getTokenSet());
				AgentHelper.sendSimpleMessage(EnvironmentAgent.this, subscribers, ACLMessage.PROPAGATE, new BetNotification(request.getPlayerAID(), request.getTokenSet()));
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage());
			} catch (InvalidTokenAmountException e) {
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.FAILURE, new FailureMessage(e.getMessage()));
			}			
			return true;
		}
		
	}
}
