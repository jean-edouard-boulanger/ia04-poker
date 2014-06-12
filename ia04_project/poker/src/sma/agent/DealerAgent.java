package sma.agent;

import java.util.ArrayList;
import java.util.HashMap;

import poker.card.model.CardDeck;
import poker.game.exception.NoPlaceAvailableException;
import poker.game.exception.NotRegisteredPlayerException;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.model.HandStep;
import poker.game.model.PlayersContainer;
import poker.game.model.PlayersContainer.PlayerCircularIterator;
import poker.game.player.model.Player;
import poker.game.player.model.PlayerStatus;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.SubscriptionOKMessage;
import sma.message.dealer.request.DealRequest;
import sma.message.environment.notification.DealerChangedNotification;
import sma.message.environment.notification.PlayerFoldedNotification;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.request.AddCommunityCardRequest;
import sma.message.environment.request.DealCardToPlayerRequest;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class DealerAgent extends Agent {

	PlayersContainer playersContainer;
	CardDeck cardDeck;
	
	private HashMap<String, ArrayList<String>> dealTransactionsErrors;
	
	DealerMessageVisitor messageVisitor;
	
	public DealerAgent(){
		super();
		this.dealTransactionsErrors = new HashMap<String, ArrayList<String>>();
	}
	
	public void setup(){
		DFServiceHelper.registerService(this, "DealerAgent","Dealer");
		this.addBehaviour(new ReceiveEnvironmentNotificationBehaviour(this));
		this.addBehaviour(new DealCardsBehaviour());
	}
	
	private void registerDealTransaction(String cid){
		if(!dealTransactionsErrors.containsKey(cid)){
			dealTransactionsErrors.put(cid, null);
		}
	}
	
	private void addErrorForDealTransaction(String cid, String error){
		if(!dealTransactionsErrors.containsKey(cid)){
			dealTransactionsErrors.put(cid, null);
		}
		dealTransactionsErrors.get(cid).add(error);
	}
	
	private ArrayList<String> getErrorsForDealTransaction(String cid){
		return this.dealTransactionsErrors.get(cid);
	}
	
	private void releaseDealTransaction(String cid){
		this.dealTransactionsErrors.remove(cid);
	}
	
	
	private class ReceiveEnvironmentNotificationBehaviour extends CyclicBehaviour{
		
		private DealerAgent dealerAgent;
		private AID environment;
		
		public ReceiveEnvironmentNotificationBehaviour(DealerAgent agent){
			super(agent);
			this.dealerAgent = agent;
			this.environment = DFServiceHelper.searchService(dealerAgent,"PokerEnvironment", "Environment");
			subscribeToEnvironment();
		}
		
		/**
		 * Handle environment events.
		 */
		@Override
		public void action() {
			
			boolean msgReceived = AgentHelper.receiveMessage(this.myAgent,MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE), new MessageVisitor(){
				
				// we update players list, players status change, dealer change 
				
				@Override
				public boolean onPlayerSitOnTableNotification(PlayerSitOnTableNotification notif, ACLMessage aclMsg) {
					try {
						playersContainer.addPlayer(notif.getNewPlayer());
					} catch (PlayerAlreadyRegisteredException | NoPlaceAvailableException e) {
						System.out.println("[" + myAgent.getLocalName() + "] can't add player, environment inconsistency.");
					}
					return true;
				}
				
				@Override
				public boolean onPlayerFoldedNotification(PlayerFoldedNotification notif, ACLMessage aclMsg) {
					Player player = playersContainer.getPlayerByAID(notif.getPlayerAID());
					if(player != null)
						player.setStatus(PlayerStatus.FOLDED);
					else
						System.out.println("[" + myAgent.getLocalName() + "] can't fold '" + notif.getPlayerAID().getLocalName() + "', not a player, environment inconsistency.");
					
					return true;
				}
				
				@Override
				public boolean onDealerChangedNotification(DealerChangedNotification notif, ACLMessage aclMsg) {
					Player dealer = playersContainer.getPlayerByAID(notif.getDealer());
					try {
						playersContainer.setDealer(dealer);
					} catch (NotRegisteredPlayerException e) {
						System.out.println("[" + myAgent.getLocalName() + "] the dealer '" + notif.getDealer().getLocalName() + "' is not a player, environment inconsistency.");
					}
					return true;
				}
				
				// TODO: player out
				
				// All other environment changes are discarded.
				@Override
				public boolean onEnvironmentChanged(Message notif, ACLMessage aclMsg) {	return true; }
			});
			
			if(!msgReceived)
				block();
		}
		
		private void subscribeToEnvironment(){
			
			TransactionBhv envSubscriptionBhv = new TransactionBhv(dealerAgent, null, environment, ACLMessage.SUBSCRIBE);
			envSubscriptionBhv.setResponseVisitor(new MessageVisitor(){
				
				@Override
				public boolean onSubscriptionOK(SubscriptionOKMessage msg, ACLMessage aclMsg) {
					System.out.println("[" + dealerAgent.getLocalName() + "] subscription to environment succeded.");
					// Initialization of the initial player container.
					playersContainer = msg.getGame().getPlayersContainer();
					return true;
				}
				
				@Override
				public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
					System.out.println("[" + dealerAgent.getLocalName() + "] subscription to environment failed: " + msg.getMessage());
					return true;
				}
				
			});
			dealerAgent.addBehaviour(envSubscriptionBhv);
		}
		
	}
	
	private class DealCardsBehaviour extends Behaviour{

		@Override
		public void action() {
			if(!AgentHelper.receiveMessage(DealerAgent.this, ACLMessage.REQUEST, messageVisitor)){
				block();
			}
		}

		@Override
		public boolean done() {
			return false;
		}
		
	}
	
	private class ConcludeDealPlayersCardsTransactionBehaviour extends OneShotBehaviour{

		private ACLMessage request;
		
		public ConcludeDealPlayersCardsTransactionBehaviour(ACLMessage request){
			this.request = request;
		}
		
		@Override
		public void action() {
			
			Message conclusionMessage = null;
			int performative = ACLMessage.INFORM;
			
			ArrayList<String> errors = getErrorsForDealTransaction(request.getConversationId());
			
			if(getErrorsForDealTransaction(request.getConversationId()) != null){
				conclusionMessage = new FailureMessage();
				performative = ACLMessage.FAILURE;
				
				StringBuffer sb = new StringBuffer();
				for(String error : errors){
					sb.append(error).append(System.lineSeparator());
				}
				
				((FailureMessage)conclusionMessage).setMessage(sb.toString());
			}
			else {
				conclusionMessage = new OKMessage();
			}
			
			AgentHelper.sendReply(DealerAgent.this, this.request, performative, conclusionMessage);
			releaseDealTransaction(this.request.getConversationId());
		}
		
	}
	
	private class DealerMessageVisitor extends MessageVisitor{
		
		public boolean onPlayerSitOnTableNotification(PlayerSitOnTableNotification notification, ACLMessage aclMsg){
			try{
				playersContainer.addPlayer(notification.getNewPlayer());
			}catch(Exception ex){
				ex.printStackTrace();
			}		
			return true;
		}
		
		public boolean onDealRequest(DealRequest request, ACLMessage aclMsg){

			AID EnvAID = DFServiceHelper.searchService(DealerAgent.this, "PokerEnvironment", "Environment");
			
			registerDealTransaction(aclMsg.getConversationId());

			SequentialBehaviour globalTransactionBehaviour = new SequentialBehaviour();
			ParallelBehaviour parallelTransactions = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);
			
			globalTransactionBehaviour.addSubBehaviour(parallelTransactions);
			globalTransactionBehaviour.addSubBehaviour(new ConcludeDealPlayersCardsTransactionBehaviour(aclMsg));
			
			HandStep handStep = request.getHandStep();
			if(handStep == HandStep.PLAYER_CARDS_DEAL)
			{
				//TODO: Start with the small blind
				PlayerCircularIterator it = playersContainer.getCircularIterator();
				while(it.getLoopNumber() < 3){
					DealCardToPlayerRequest dealRequest = new DealCardToPlayerRequest(it.next().getAID(), cardDeck.pickCard());
					
					TransactionBhv dealSingleCardBhv = new TransactionBhv(DealerAgent.this, dealRequest, EnvAID);
					dealSingleCardBhv.setResponseVisitor(new MessageVisitor(){
						@Override
						public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
							addErrorForDealTransaction(aclMsg.getConversationId(), msg.getMessage());
							return true;
						}
					});
					
					parallelTransactions.addSubBehaviour(dealSingleCardBhv);
				}
				
				addBehaviour(globalTransactionBehaviour);
			}
			else if(handStep == HandStep.FLOP || handStep == HandStep.TURN || handStep == HandStep.RIVER){
				int nbDealtCards = 1;
				if(handStep == HandStep.FLOP){
					nbDealtCards = 3;
				}
				
				for(int i = 0; i < nbDealtCards; i++){
					AddCommunityCardRequest addCardRequest = new AddCommunityCardRequest(cardDeck.pickCard());
					
					TransactionBhv dealSingleCardBhv = new TransactionBhv(DealerAgent.this, addCardRequest, EnvAID);
					dealSingleCardBhv.setResponseVisitor(new MessageVisitor(){
						@Override
						public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
							addErrorForDealTransaction(aclMsg.getConversationId(), msg.getMessage());
							return true;
						}
					});
					
					parallelTransactions.addSubBehaviour(dealSingleCardBhv);
				}
				
				addBehaviour(globalTransactionBehaviour);
			}
			else 
			{
				AgentHelper.sendReply(DealerAgent.this, aclMsg, ACLMessage.FAILURE, new FailureMessage("No card dealt at that step"));
			}
			
			return true;	
		}
	}	
}
