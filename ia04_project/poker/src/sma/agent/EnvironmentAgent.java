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
import poker.game.model.BlindValueDefinition;
import poker.game.player.model.Player;
import poker.token.model.TokenValueDefinition;
import sma.agent.helper.DFServiceHelper;
import sma.agent.simulationAgent.PlayerSubscriptionBhv;
import sma.message.Message;
import sma.message.MessageVisitor;

public class EnvironmentAgent extends Agent {
	
	private ArrayList<Player> players;
	private CommunityCards communityCards;
	private BlindValueDefinition blindValueDefinition;
	private TokenValueDefinition tokenValueDefinition;
	private int currentPlayerIndex;
	
	private EnvironmentMessageVisitor msgVisitor;
	
	public EnvironmentAgent(){
		this.players = new ArrayList<Player>();
		//this.communityCards = new CommunityCards();
		this.blindValueDefinition = new BlindValueDefinition();
		this.tokenValueDefinition = new TokenValueDefinition();
		this.msgVisitor = new EnvironmentMessageVisitor();
	}
	
	@Override
	public void setup()
	{
		super.setup();
		DFServiceHelper.registerService(this, "PokerEnvironment","Environment");
	}
		
	private Player getCurrentPlayer(){
		return this.players.get(currentPlayerIndex);
	}
	
	private Player getPlayerByAID(AID aid){
		for(Player p : this.players){
			if(p.getAID().equals(aid)){
				return p;
			}
		}
		return null;
	}
	
	private Player setCurrentPlayerIndex(int currentPlayerIndex){
		this.currentPlayerIndex = currentPlayerIndex;
		return this.getCurrentPlayer();
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
		
	}
	
}
