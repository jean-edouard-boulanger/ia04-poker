package sma.agent;

import gui.player.PlayerWindow.PlayerGuiEvent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import poker.game.model.BetContainer;
import poker.game.model.Game;
import poker.game.model.PlayersContainer;
import poker.game.player.model.Player;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.SubscriptionOKMessage;
import sma.message.bet.request.BetRequest;
import sma.message.environment.notification.PlayerReceivedTokenSetNotification;

public class BetManagerAgent extends Agent {

	BetContainer betContainer;
	BetManagerMessageVisitor msgVisitor;
	Game game;
	
	public BetManagerAgent(){
		super();
		betContainer = new BetContainer();
		game = new Game();
	}
	
	public void setup(){
		super.setup();
		DFServiceHelper.registerService(this, "BetManagerAgent","BetManager");
		
		this.addBehaviour(new ReceiveRequestBehaviour(this));
	}
	
	private class ReceiveRequestBehaviour extends CyclicBehaviour {
		
		private AID environment;
		
		public ReceiveRequestBehaviour(Agent agent) {
			this.myAgent = agent;
			this.environment = DFServiceHelper.searchService(myAgent, "PokerEnvironment", "Environment");
			subscribeToEnvironment();
		}
		
		@Override
		public void action() {

			boolean msgReceived = AgentHelper.receiveMessage(this.myAgent, ACLMessage.REQUEST, ((BetManagerAgent)myAgent).getMsgVisitor());
			
			if(!msgReceived)
				block();
		}
		
		private void subscribeToEnvironment(){
			
			TransactionBhv envSubscriptionBhv = new TransactionBhv(myAgent, null, environment, ACLMessage.SUBSCRIBE);
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
	
	private class BetManagerMessageVisitor extends MessageVisitor {	
		
		@Override
		public boolean onPlayerReceivedTokenSetNotification(PlayerReceivedTokenSetNotification notification, ACLMessage aclMsg){
			
			Player player = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID());
			player.setTokens(notification.getReceivedTokenSet());
			
			return true;
		}
		
		@Override
		public boolean onBetRequest(BetRequest request, ACLMessage aclMsg) {
			
			
			
			return true;
		}
	}

	public BetManagerMessageVisitor getMsgVisitor() {
		return msgVisitor;
	}

	public void setMsgVisitor(BetManagerMessageVisitor msgVisitor) {
		this.msgVisitor = msgVisitor;
	}
}
