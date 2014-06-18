package sma.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import poker.card.exception.CommunityCardsFullException;
import poker.card.heuristics.combination.exception.EmptyCardListException;
import poker.card.heuristics.combination.helper.CardCombinations;
import poker.card.heuristics.combination.helper.HandComparator;
import poker.card.heuristics.combination.model.Hand;
import poker.card.model.Card;
import poker.card.model.CommunityCards;
import poker.game.player.model.Player;
import poker.game.player.model.PlayerStatus;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBehaviour;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.SubscriptionOKMessage;
import sma.message.determine_winner.DetermineWinnerRequest;
import sma.message.environment.notification.CardAddedToCommunityCardsNotification;
import sma.message.environment.notification.CommunityCardsEmptiedNotification;
import sma.message.environment.notification.DealerChangedNotification;
import sma.message.environment.notification.PlayerCardsRevealedNotification;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.notification.WinnerDeterminedNotification;

public class DetermineWinnerAgent extends Agent {
		
	private CommunityCards communityCards;
	private DetermineWinnerMessageVisitor msgVisitor;
	private ArrayList<Player> players;
	private AID environment;
	
	public DetermineWinnerAgent() {
		this.msgVisitor = new DetermineWinnerMessageVisitor();
		this.communityCards = new CommunityCards();
		players = new ArrayList<Player>();
	}
	
	@Override
	public void setup()
	{
		super.setup();
		DFServiceHelper.registerService(this, "DetermineWinnerAgent","DetermineWinner");
		this.environment = DFServiceHelper.searchService(this, "PokerEnvironment", "Environment");
		
		this.addBehaviour(new ReceiveRequestBehaviour(this));
		this.addBehaviour(new ReceiveNotificationBehaviour(this));
	}
	
	private class DetermineWinnerMessageVisitor extends MessageVisitor {
		@Override
		public boolean onCommunityCardsEmptiedNotification(CommunityCardsEmptiedNotification notif, ACLMessage aclMsg) {
			communityCards.popCards();
			
			return true;
		}
		
		@Override
		public boolean onCardAddedToCommunityCardsNotification(CardAddedToCommunityCardsNotification notification, ACLMessage aclMsg) {
			try {
				communityCards.pushCard(notification.getNewCommunityCard());
			} catch (CommunityCardsFullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		}
		
		@Override
		public boolean onPlayerCardsRevealedNotification(PlayerCardsRevealedNotification playerCardsRevealed, ACLMessage aclMsg) {
			
			//Adding player to potential winners if still in game (did not fold, not out)
			if(playerCardsRevealed.getPlayer().getStatus() == PlayerStatus.IN_GAME)
				players.add(playerCardsRevealed.getPlayer());
			
			return true;
		}
		
		@Override
		public boolean onDetermineWinnerRequest(DetermineWinnerRequest request, ACLMessage aclMsg) {
			
			Map<AID, Hand> winners = determineRoundWinners();
			
			AgentHelper.sendSimpleMessage(DetermineWinnerAgent.this, environment, ACLMessage.INFORM, new WinnerDeterminedNotification(winners));
			
			//Sending the list of winners (could be more than one winner)
			AgentHelper.sendReply(DetermineWinnerAgent.this, aclMsg, ACLMessage.INFORM, new WinnerDeterminedNotification(winners));
			
			//Winner was determined for the current round
			players.clear();
			
			return true;
		}
		
		@Override
		public boolean onPlayerSitOnTableNotification(PlayerSitOnTableNotification notification, ACLMessage aclMsg) {
			
			players.add(notification.getNewPlayer());
			
			return true;
		}
	}
	
	private Map<AID, Hand> determineRoundWinners() {

		Map<AID, Hand> playerHandMap = new HashMap<AID, Hand>();

		ArrayList<Hand> winningHands = new ArrayList<Hand>();
		
		for(Player p : players) {
			ArrayList<Card> playerHandCards = p.getDeck().getCards();
			playerHandCards.addAll(communityCards.getCommunityCards());
			
			try {
				//Determine the best hand of the current player and the player and its hand to the map. Adding the hand to a list of hands to compare them easily.
				Hand h = CardCombinations.bestHandFromCards(playerHandCards);
				playerHandMap.put(p.getAID(), h);
				winningHands.add(h);
			} catch (EmptyCardListException e) {
				e.printStackTrace();
			}
		}

		//Getting the best/winning hands (more than one in case of equality)
		winningHands = HandComparator.bestHand(winningHands);
		
		Map<AID, Hand> winners = new HashMap<AID, Hand>();
		
		//Only keeping players with a winning hand
		for(Hand h : winningHands) {
			for(Entry<AID, Hand> entry : playerHandMap.entrySet()) {
				if(h == entry.getValue()) {
					winners.put(entry.getKey(), entry.getValue());
				}
			}
		}
				
		return winners;
	}
	
	private class ReceiveRequestBehaviour extends CyclicBehaviour {

		private AID environment;
		
		public ReceiveRequestBehaviour(Agent agent) {
			myAgent = agent;
			this.environment = DFServiceHelper.searchService(myAgent,"PokerEnvironment", "Environment");
			subscribeToEnvironment();
		}
		
		@Override
		public void action() {
			boolean msgReceived = AgentHelper.receiveMessage(this.myAgent, ACLMessage.REQUEST, ((DetermineWinnerAgent)myAgent).getMsgVisitor());
			
			if(!msgReceived)
				block();
		}
		
		
		private void subscribeToEnvironment(){
						
			TransactionBehaviour envSubscriptionBhv = new TransactionBehaviour(myAgent, null, environment, ACLMessage.SUBSCRIBE);
			envSubscriptionBhv.setResponseVisitor(new MessageVisitor(){
				
				@Override
				public boolean onSubscriptionOK(SubscriptionOKMessage msg, ACLMessage aclMsg) {
					System.out.println("[" + myAgent.getLocalName() + "] subscription to environment succeded.");
					return true;
				}
				
				@Override
				public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
					System.out.println("[" + myAgent.getLocalName() + "] subscription to environment failed: " + msg.getMessage());
					return true;
				}
				
			});
			myAgent.addBehaviour(envSubscriptionBhv);
		}
	}

	private class ReceiveNotificationBehaviour extends CyclicBehaviour {
		public ReceiveNotificationBehaviour(Agent agent) {
			myAgent = agent;
		}
		
		@Override
		public void action() {
			boolean msgReceived = AgentHelper.receiveMessage(this.myAgent, ACLMessage.PROPAGATE, ((DetermineWinnerAgent)myAgent).getMsgVisitor());
			
			if(!msgReceived)
				block();
		}	
	}
	
	public CommunityCards getCommunityCards() {
		return communityCards;
	}

	public void setCommunityCards(CommunityCards communityCards) {
		this.communityCards = communityCards;
	}

	public DetermineWinnerMessageVisitor getMsgVisitor() {
		return msgVisitor;
	}

	public void setMsgVisitor(DetermineWinnerMessageVisitor msgVisitor) {
		this.msgVisitor = msgVisitor;
	}
}