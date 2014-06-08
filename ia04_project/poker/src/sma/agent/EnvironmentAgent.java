package sma.agent;

import gui.server.ServerWindow;

import java.io.IOException;
import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import poker.card.model.CommunityCards;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.model.BlindValueDefinition;
import poker.game.model.Game;
import poker.game.player.model.Player;
import poker.token.model.TokenValueDefinition;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.simulationAgent.PlayerSubscriptionBhv;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
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
			ACLMessage receivedMessage = this.myAgent.receive();
			if(receivedMessage != null){
				addBehaviour(new EnvironmentHandleRequestBehaviour(receivedMessage));
			}
			else{
				block();
			}
		}

		@Override
		public boolean done() {
			return false;
		}
	}
	
	public class EnvironmentHandleRequestBehaviour extends Behaviour{
		
		private int step = 0;
		private ACLMessage receivedRequestMessage;
		private Message handledRequestMessage;
		
		public EnvironmentHandleRequestBehaviour(ACLMessage receivedRequestMessage){
			super();
			this.receivedRequestMessage = receivedRequestMessage;
		}
		
		@Override
		public void action() {
			if(this.step == 0){
				try{
					this.handledRequestMessage = Message.fromJson(this.receivedRequestMessage.getContent());
					this.step++;
				}
				catch(IOException ex){
					this.step = 2;
				}
			}
			else if(this.step == 1){
				
			}
			else
			{
				this.step = 2;
			}
		}

		@Override
		public boolean done() {
			return step == 2;
		}
	}
	
	private class EnvironmentMessageVisitor extends MessageVisitor{

		@Override
		public boolean onAddPlayerTableRequest(AddPlayerTableRequest request, ACLMessage aclMsg) {
			
			try{
				game.addPlayer(request.getNewPlayer());
			}
			catch(PlayerAlreadyRegisteredException ex){
				AgentHelper.sendReply(EnvironmentAgent.this, aclMsg, ACLMessage.FAILURE, new FailureMessage(ex.getMessage()));
				return true;
			}
			
			for(Player p : game.getGamePlayers()){
				AgentHelper.sendSimpleMessage(EnvironmentAgent.this, p.getAID(), ACLMessage.INFORM, new PlayerSitOnTableNotification(request.getNewPlayer(), p.getAID()));
			}
			
			return true;
		}
	}
}
