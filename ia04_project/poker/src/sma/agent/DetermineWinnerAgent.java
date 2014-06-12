package sma.agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

import poker.card.exception.CommunityCardsFullException;
import poker.card.model.CommunityCards;
import poker.game.player.model.Player;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.message.MessageVisitor;
import sma.message.determine_winner.DetermineWinnerRequest;
import sma.message.environment.notification.CardAddedToCommunityCardsNotification;
import sma.message.environment.notification.CommunityCardsEmptiedNotification;
import sma.message.environment.notification.PlayerCardsRevealedNotification;
import sma.message.environment.notification.WinnerDeterminedNotification;

public class DetermineWinnerAgent extends Agent {
		
	private CommunityCards communityCards;
	private DetermineWinnerMessageVisitor msgVisitor;
	private ArrayList<Player> players;
	
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
			players.add(playerCardsRevealed.getPlayer());
			
			return true;
		}
		
		@Override
		public boolean onDetermineWinnerRequest(DetermineWinnerRequest request, ACLMessage aclMsg) {
			//Determine the winner
			ArrayList<Player> winners = new ArrayList<Player>();
			
			//Sending the list of winners (could be more than one winner)
			AgentHelper.sendReply(DetermineWinnerAgent.this, aclMsg, ACLMessage.INFORM, new WinnerDeterminedNotification(winners));
			
			//Winner was determined for the current round
			players.clear();
			
			return true;
		}
	}
	
	private class ReceiveRequestBehaviour extends CyclicBehaviour {

		public ReceiveRequestBehaviour(Agent agent) {
			myAgent = agent;
		}
		
		@Override
		public void action() {
			boolean msgReceived = AgentHelper.receiveMessage(this.myAgent, ACLMessage.REQUEST, ((DetermineWinnerAgent)myAgent).getMsgVisitor());
			
			if(!msgReceived)
				block();
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