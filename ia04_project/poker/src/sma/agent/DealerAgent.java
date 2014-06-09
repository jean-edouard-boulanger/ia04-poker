package sma.agent;

import java.util.ArrayList;

import poker.card.model.CardDeck;
import poker.game.model.PlayersContainer;
import poker.game.model.PlayersContainer.PlayerCircularIterator;
import poker.game.player.model.Player;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.dealer.request.DealRequest;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.request.DealCardToPlayerRequest;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;

public class DealerAgent extends Agent {

	PlayersContainer playersContainer;
	CardDeck cardDeck;
	
	DealerMessageVisitor messageVisitor;
	
	public void setup(){
		this.addBehaviour(new ReceiveEnvironmentNotificationBehaviour());
		this.addBehaviour(new DealCardsBehaviour());
	}
	
	private class ReceiveEnvironmentNotificationBehaviour extends Behaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	private class DealCardsBehaviour extends Behaviour{

		@Override
		public void action() {
			
		}

		@Override
		public boolean done() {
			return false;
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
			switch (request.getHandStep()) {
			case PLAYER_CARDS_DEAL:
				
				SequentialBehaviour globalTransactionBehaviour = new SequentialBehaviour();
				ParallelBehaviour parallelTransactions = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);
				
				globalTransactionBehaviour.addSubBehaviour(parallelTransactions);
				globalTransactionBehaviour.addSubBehaviour(new OneShotBehaviour() {
					
					@Override
					public void action() {
						//TODO: Send OK message
					}
					
				});
				
				//TODO: Start with the small blind
				PlayerCircularIterator it = playersContainer.getCircularIterator();
				while(it.getLoopNumber() < 3){
					DealCardToPlayerRequest dealRequest = new DealCardToPlayerRequest(it.next().getAID(), cardDeck.pickCard());
					parallelTransactions.addSubBehaviour(new TransactionBhv(DealerAgent.this, dealRequest, new AID("environment", AID.ISLOCALNAME)));
				}
				
				addBehaviour(globalTransactionBehaviour);
				
				break;
			case FLOP:
				//TODO: Implement flop deal
				break;
			case TURN:
				//TODO: Implement turn deal
				break;
			case RIVER:
				//TODO: Implement river deal
				break;
			default:
				AgentHelper.sendReply(DealerAgent.this, aclMsg, ACLMessage.FAILURE, new FailureMessage("No cards dealt at that step"));
				break;
			}
			
			return true;	
		}
	}
}
