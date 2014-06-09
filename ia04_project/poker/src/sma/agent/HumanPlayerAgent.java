package sma.agent;

import gui.player.PlayerWindow;
import gui.player.PlayerWindow.PlayerGuiEvent;
import gui.player.WaitGameWindow;
import gui.player.WaitGameWindow.WaitGameGuiEvent;
import gui.server.ServerWindow;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import poker.card.exception.CommunityCardsFullException;
import poker.game.model.Game;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.PlayerSubscriptionRequest;
import sma.message.environment.notification.CardAddedToCommunityCardsNotification;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFHSQLKB;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HumanPlayerAgent extends GuiAgent {

	private PropertyChangeSupport changes_waitgame = new PropertyChangeSupport(this);
	private PropertyChangeSupport changes_game = new PropertyChangeSupport(this);
	
	private Game game;
	
	private HumanPlayerRequestMessageVisitor msgVisitor_request;
	private HumanPlayerFailureMessageVisitor msgVisitor_failure;
	
	private WaitGameWindow wait_game_window;
	
	public void setup()
	{
		super.setup();
		
		game = new Game();
		
		this.msgVisitor_request = new HumanPlayerRequestMessageVisitor();
		this.msgVisitor_failure = new HumanPlayerFailureMessageVisitor();

		PlayerWindow player_window = new PlayerWindow();
		player_window.setHumanPlayerAgent(this);
		changes_game.addPropertyChangeListener(player_window);
		PlayerWindow.launchWindow(new String[]{});
		
		wait_game_window = new WaitGameWindow(this);
		changes_waitgame.addPropertyChangeListener(wait_game_window);
		
		addBehaviour(new HumanPlayerReceiveRequestBehaviour(this));
		addBehaviour(new HumanPlayerReceiveFailureBehaviour(this));

	}
	
	 /**************************************
     *  Listening request
     */
	private class HumanPlayerReceiveRequestBehaviour extends CyclicBehaviour{
		
		MessageTemplate receiveRequestMessageTemplate;
		
		public HumanPlayerReceiveRequestBehaviour(Agent agent){
			super(agent);
			this.receiveRequestMessageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		}
		
		@Override
		public void action() {
			if(!AgentHelper.receiveMessage(this.myAgent, receiveRequestMessageTemplate, msgVisitor_request)){
				block();
			}
		}
	}
	
	 /**************************************
     *  Listening failure
     */
	private class HumanPlayerReceiveFailureBehaviour extends CyclicBehaviour{
		
		MessageTemplate receiveFailureMessageTemplate;
		
		public HumanPlayerReceiveFailureBehaviour(Agent agent){
			super(agent);
			this.receiveFailureMessageTemplate = MessageTemplate.MatchPerformative(ACLMessage.FAILURE);
		}
		
		@Override
		public void action() {
			if(!AgentHelper.receiveMessage(this.myAgent, receiveFailureMessageTemplate, msgVisitor_failure)){
				block();
			}
		}
	}

	/**************************************
     *  Request message visitor
     */
	private class HumanPlayerRequestMessageVisitor extends MessageVisitor {
		
		@Override
		public boolean onCardAddedToCommunityCardsNotification(CardAddedToCommunityCardsNotification notification, ACLMessage aclMsg) {
			
			try {
				
				game.getCommunityCards().pushCard(notification.getNewCommunityCard());
				
				changes_game.firePropertyChange(PlayerGuiEvent.ADD_COMMUNITY_CARD.toString(), null, notification.getNewCommunityCard());
				
			} catch (CommunityCardsFullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		}
		
	}
	
	/**************************************
     *  Failure message visitor
     */
	private class HumanPlayerFailureMessageVisitor extends MessageVisitor {
		
		@Override
		public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {

			/**!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			 * A AMELIORER, DISTINCTION ENTRE LES ERREURS
			 */
			changes_waitgame.firePropertyChange(WaitGameGuiEvent.FAILURE_CONNECT.toString(), null, null);
			
			return true;	
		}
	}
	
	/**************************************
     *  Evenement depuis IHM
     */
	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		/**************************************
	     *  IHM WaitGame
	     */
		if(arg0.getType() == WaitGameGuiEvent.TRY_CONNECT.ordinal())
		{
			
			String pseudo = (String) arg0.getParameter(0);
			AID simulation = DFServiceHelper.searchService(this, "PokerSimulation","Simulation");
			this.addBehaviour(new TransactionBhv(this, new PlayerSubscriptionRequest(pseudo), simulation, ACLMessage.SUBSCRIBE));
		}
		
		else if(arg0.getType() == WaitGameGuiEvent.GAME_START.ordinal())
		{
			wait_game_window.setVisible(false);
			
			PlayerWindow player_window = new PlayerWindow();
			player_window.setHumanPlayerAgent(this);
			changes_game.addPropertyChangeListener(player_window);
			PlayerWindow.launchWindow(new String[]{});
		}
		
		/**************************************
	     *  IHM Player
	     */
	}
	
	
	private static String generateNickName(){
		//TODO: To be improved...
		return Long.toHexString(Double.doubleToLongBits(Math.random()));
	}
}
