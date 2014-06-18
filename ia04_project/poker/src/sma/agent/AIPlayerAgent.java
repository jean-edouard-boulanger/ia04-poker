package sma.agent;

import gui.player.PlayerWindow;
import gui.player.PlayerWindow.PlayerGuiEvent;
import gui.player.WaitGameWindow;
import gui.player.WaitGameWindow.WaitGameGuiEvent;
import gui.player.event.model.PlayRequestEventData;
import gui.player.event.model.PlayerBetEventData;
import gui.player.event.model.PlayerTokenSetChangedEventData;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.beans.PropertyChangeSupport;

import javafx.embed.swing.JFXPanel;

import javax.swing.SwingUtilities;

import poker.card.exception.CommunityCardsFullException;
import poker.card.exception.UserDeckFullException;
import poker.game.exception.NoPlaceAvailableException;
import poker.game.exception.NotRegisteredPlayerException;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.model.BetType;
import poker.game.model.Game;
import poker.game.player.model.Player;
import poker.game.player.model.PlayerStatus;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.helpers.TokenSetValueEvaluator;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import sma.agent.aiplayeragent.messagevisitor.AIPlayerAgentMessageVisitor;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBehaviour;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.PlayerSubscriptionRequest;
import sma.message.SubscriptionOKMessage;
import sma.message.bet.notification.BetsMergedNotification;
import sma.message.bet.request.BetRequest;
import sma.message.bet.request.FoldRequest;
import sma.message.environment.notification.BetNotification;
import sma.message.environment.notification.BlindValueDefinitionChangedNotification;
import sma.message.environment.notification.CardAddedToCommunityCardsNotification;
import sma.message.environment.notification.CommunityCardsEmptiedNotification;
import sma.message.environment.notification.CurrentPlayerChangedNotification;
import sma.message.environment.notification.DealerChangedNotification;
import sma.message.environment.notification.PlayerCheckNotification;
import sma.message.environment.notification.PlayerReceivedCardNotification;
import sma.message.environment.notification.PlayerReceivedTokenSetNotification;
import sma.message.environment.notification.PlayerReceivedUnknownCardNotification;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.notification.PlayerStatusChangedNotification;
import sma.message.environment.notification.TokenValueDefinitionChangedNotification;
import sma.message.environment.request.PlayerFoldedRequest;
import sma.message.simulation.request.PlayRequest;

public class AIPlayerAgent extends Agent {

	private Game game;

	private AIPlayerAgentMessageVisitor msgVisitor;
	private AIPlayerFailureMessageVisitor msgVisitor_failure;

	private ACLMessage playRequestMessage;
	
	public void setup()
	{
		//super.setup();

		game = new Game();

		this.msgVisitor = new AIPlayerAgentMessageVisitor(game, this);
		this.msgVisitor_failure = new AIPlayerFailureMessageVisitor();

		addBehaviour(new AIPlayerReceiveNotificationBehaviour(this));
		addBehaviour(new AIPlayerReceiveRequestBehaviour(this));
		addBehaviour(new AIPlayerReceiveFailureBehaviour(this));
	}

	/**************************************
	 *  Listening notifications
	 */
	private class AIPlayerReceiveNotificationBehaviour extends CyclicBehaviour{

		MessageTemplate receiveNotificationMessageTemplate;

		public AIPlayerReceiveNotificationBehaviour(Agent agent){
			super(agent);
			this.receiveNotificationMessageTemplate = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
		}

		@Override
		public void action() {
			if(!AgentHelper.receiveMessage(this.myAgent, receiveNotificationMessageTemplate, msgVisitor)){
				block();
			}
		}
	}

	/**************************************
	 *  Listening request
	 */
	private class AIPlayerReceiveRequestBehaviour extends CyclicBehaviour{

		MessageTemplate receiveRequestMessageTemplate;

		public AIPlayerReceiveRequestBehaviour(Agent agent){
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
	
	/**************************************
	 *  Listening failure
	 */
	private class AIPlayerReceiveFailureBehaviour extends CyclicBehaviour{

		MessageTemplate receiveFailureMessageTemplate;

		public AIPlayerReceiveFailureBehaviour(Agent agent){
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
	 *  Failure message visitor
	 */
	private class AIPlayerFailureMessageVisitor extends MessageVisitor {

		@Override
		public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {

			System.out.println("[" + getLocalName() + "] Received falure message with content: '" + msg.getMessage() + "'.");
			
			return true;
		}
	}

	/**************************************
	 *  Private functions related to IHM events
	 */

	private void replyToSimulationPlayRequest(int betAmount) {
		if(playRequestMessage != null) {							
			//Answering to simulation play request
			AgentHelper.sendReply(this, playRequestMessage, ACLMessage.REQUEST, new BetRequest(betAmount, getAID()));
			
			//Setting play request to null, waiting for a new request from simulation
			playRequestMessage = null;
		}
	}
	
	public ACLMessage getPlayRequestMessage() {
		return playRequestMessage;
	}

	public void setPlayRequestMessage(ACLMessage playRequestMessage) {
		this.playRequestMessage = playRequestMessage;
	}
}
