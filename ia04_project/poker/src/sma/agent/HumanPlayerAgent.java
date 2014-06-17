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
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.beans.PropertyChangeSupport;

import javafx.embed.swing.JFXPanel;

import javax.swing.SwingUtilities;

import poker.card.exception.CommunityCardsFullException;
import poker.game.exception.NoPlaceAvailableException;
import poker.game.exception.NotRegisteredPlayerException;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.model.BetType;
import poker.game.model.Game;
import poker.game.player.model.Player;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.helpers.TokenSetValueEvaluator;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBehaviour;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.PlayerSubscriptionRequest;
import sma.message.SubscriptionOKMessage;
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

public class HumanPlayerAgent extends GuiAgent {

	private PropertyChangeSupport changes_waitgame = new PropertyChangeSupport(this);
	private PropertyChangeSupport changes_game = new PropertyChangeSupport(this);

	private Game game;

	private HumanPlayerRequestMessageVisitor msgVisitor_request;
	private HumanPlayerFailureMessageVisitor msgVisitor_failure;

	private WaitGameWindow wait_game_window;
	private PlayerWindow player_window;

	private ACLMessage playRequestMessage;
	
	public void setup()
	{
		//super.setup();

		game = new Game();

		this.msgVisitor_request = new HumanPlayerRequestMessageVisitor();
		this.msgVisitor_failure = new HumanPlayerFailureMessageVisitor();


		wait_game_window = new WaitGameWindow(this);
		changes_waitgame.addPropertyChangeListener(wait_game_window);

		//Need to init the window via the SwingUtilities.invokeLater method on Mac to work

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new JFXPanel();
				javafx.application.Platform.runLater(new Runnable() {

					@Override
					public void run() {
						HumanPlayerAgent.this.player_window = PlayerWindow.launchWindow(HumanPlayerAgent.this, changes_game);
					}
				});

			}
		});

		addBehaviour(new HumanPlayerReceiveRequestBehaviour(this));
		addBehaviour(new HumanPlayerReceiveNotificationBehaviour(this));
		addBehaviour(new HumanPlayerReceiveFailureBehaviour(this));

	}

	/**************************************
	 *  Listening notifications
	 */
	private class HumanPlayerReceiveNotificationBehaviour extends CyclicBehaviour{

		MessageTemplate receiveNotificationMessageTemplate;

		public HumanPlayerReceiveNotificationBehaviour(Agent agent){
			super(agent);
			this.receiveNotificationMessageTemplate = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
		}

		@Override
		public void action() {
			if(!AgentHelper.receiveMessage(this.myAgent, receiveNotificationMessageTemplate, msgVisitor_request)){
				block();
			}
		}
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
		public boolean onPlayerReceivedUnknownCardNotification(PlayerReceivedUnknownCardNotification notification, ACLMessage aclMsg) {

			System.out.println("[HumanPlayerAgent] Unknown card notification received. PLAYER_RECEIVED_UNKNOWN_CARD fired");	

			// if(!notification.getPlayerAID().equals(HumanPlayerAgent.this.getAID()))
			changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_RECEIVED_UNKNOWN_CARD.toString(), null, game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID()).getTablePositionIndex());

			return true;
		}

		@Override
		public boolean onPlayerReceivedCardNotification(PlayerReceivedCardNotification notification, ACLMessage aclMsg){

			// if(notification.getPlayerAID().equals(HumanPlayerAgent.this.getAID()))
			changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_RECEIVED_CARD.toString(), null, notification.getReceivedCard());

			return true;
		}

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

		@Override
		public boolean onCommunityCardsEmptiedNotification(CommunityCardsEmptiedNotification notification, ACLMessage aclMsg) {

			game.getCommunityCards().popCards();

			changes_game.firePropertyChange(PlayerGuiEvent.EMPTY_COMMUNITY_CARD.toString(), null, null);

			return true;
		}

		/*
		@Override
		public boolean onPlayerFoldedNotification(PlayerFoldedNotification notification, ACLMessage aclMsg){

			// FIND THE PLAYER NUMBER AND SEND IT WITH CONTENT
			changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_FOLDED.toString(), null, 5);

			return true;
		}
		*/

		@Override
		public boolean onPlayerStatusChangedNotification(PlayerStatusChangedNotification notification, ACLMessage aclMsg) {
			
			switch(notification.getNewStatus()){
			case FOLDED:
				changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_FOLDED.toString(), null, 5);
			default:
				break;
			}
			
			return true;
		}

		
		@Override
		public boolean onPlayerSitOnTableNotification(PlayerSitOnTableNotification notification, ACLMessage aclMsg){

			try {
				game.getPlayersContainer().addPlayer(notification.getNewPlayer());
			} catch (PlayerAlreadyRegisteredException e) {
				e.printStackTrace();
			} catch (NoPlaceAvailableException e) {
				e.printStackTrace();
			}
			// FIND THE PLAYER NUMBER AND SEND IT WITH CONTENT
			changes_game.firePropertyChange(PlayerGuiEvent.INITIALIZING_OTHER.toString(), null, notification.getNewPlayer());

			return true;
		}

		@Override
		public boolean onPlayerReceivedTokenSetNotification(PlayerReceivedTokenSetNotification notification, ACLMessage aclMsg){

			Player player = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID());
			player.setTokens(player.getTokens().addTokenSet(notification.getReceivedTokenSet()));

			PlayerTokenSetChangedEventData eventData = new PlayerTokenSetChangedEventData();
			eventData.setPlayerIndex(game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID()).getTablePositionIndex());
			eventData.setTokenSetValuation(TokenSetValueEvaluator.evaluateTokenSetValue(game.getBetContainer().getTokenValueDefinition(), player.getTokens()));

			if(notification.getPlayerAID().equals(HumanPlayerAgent.this.getAID())){
				eventData.setTokenSet(player.getTokens());
				changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_RECEIVED_TOKENSET_ME.toString(), null, eventData);
			}
			else{
				changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_RECEIVED_TOKENSET_OTHER.toString(), null, eventData);
			}

			return true;
		}

		@Override
		public boolean onBetNotification(BetNotification notification, ACLMessage aclMsg){

			// Update la mise minimum pour relancer apr�s
			TokenSet betTokenSet = notification.getBetTokenSet();
			
			game.getBetContainer().setPlayerCurrentBet(notification.getPlayerAID(), betTokenSet);
			
			Player player = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID());
			
			try {
				player.setTokens(player.getTokens().substractTokenSet(betTokenSet));
			} catch (InvalidTokenAmountException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			PlayerBetEventData eventData = new PlayerBetEventData();
			
			eventData.setTokenSetUsedForBet(betTokenSet);
			eventData.setPlayerIndex(game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID()).getTablePositionIndex());
			eventData.setBetAmount(notification.getBetAmount());

			/*if(notification.getPlayerAID().equals(HumanPlayerAgent.this.getAID())){
				eventData.setTokenSet(player.getTokens());
				changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_RECEIVED_TOKENSET_ME.toString(), null, eventData);
			}
			else{
				changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_RECEIVED_TOKENSET_OTHER.toString(), null, eventData);
			}*/
			
			changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_BET.toString(), null, eventData);
			changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_CANT_PLAY.toString(), null, eventData);
			
			return true;
		}

		@Override
		public boolean onPlayerCheckNotification(PlayerCheckNotification notification, ACLMessage aclMsg){

			// FIND THE PLAYER NUMBER AND SEND IT WITH CONTENT
			changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_CHECK.toString(), null, 5);

			return true;
		}

		@Override
		public boolean onBlindValueDefinitionChangedNotification(BlindValueDefinitionChangedNotification notification, ACLMessage aclMsg){

			game.setBlindValueDefinition(notification.getNewBlindValueDefinition());

			changes_game.firePropertyChange(PlayerGuiEvent.BLIND_VALUE.toString(), null, notification.getNewBlindValueDefinition());

			return true;
		}

		@Override
		public boolean onCurrentPlayerChangedNotification(CurrentPlayerChangedNotification notification, ACLMessage aclMsg){

			int playerIndex = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID()).getTablePositionIndex();
			changes_game.firePropertyChange(PlayerGuiEvent.CURRENT_PLAYER_CHANGED.toString(), null, game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID()));

			return true;
		}

		@Override
		public boolean onTokenValueDefinitionChangedNotification(TokenValueDefinitionChangedNotification notif, ACLMessage aclMsg) {

			game.getBetContainer().setTokenValueDefinition(notif.getTokenValueDefinition());

			changes_game.firePropertyChange(PlayerGuiEvent.INITIALIZING_MIN_TOKEN.toString(), null, game.getBetContainer().getTokenValueDefinition().getMinimumTokenValue());

			return true;
		}

		@Override
		public boolean onSubscriptionOK(SubscriptionOKMessage notif, ACLMessage aclMsg){

			game = notif.getGame();
			System.out.println("Subscription OK.");
			wait_game_window.setVisible(false);

			for(Player player : game.getPlayersContainer().getPlayers())
			{
				if(player.getAID().equals(HumanPlayerAgent.this.getAID()))
				{
					changes_game.firePropertyChange(PlayerGuiEvent.INITIALIZING_ME.toString(), null, player);
				}
				else
				{
					changes_game.firePropertyChange(PlayerGuiEvent.INITIALIZING_OTHER.toString(), null, player);
				}
			}

			return true;
		}

		@Override
		public boolean onDealerChangedNotification(DealerChangedNotification dealerChangedNotification, ACLMessage aclMsg) {

			try {
				game.getPlayersContainer().setDealer(dealerChangedNotification.getDealer());
				Player dealerPlayer = game.getPlayersContainer().getDealer();
				Player bigBlindPlayer = game.getPlayersContainer().getBigBlind();
				Player smallBlindPlayer = game.getPlayersContainer().getSmallBlind();
				
				changes_game.firePropertyChange(PlayerGuiEvent.DEALER_PLAYER_CHANGED.toString(), null, dealerPlayer);
				changes_game.firePropertyChange(PlayerGuiEvent.SMALL_BLIND_PLAYER.toString(), null, smallBlindPlayer);
				changes_game.firePropertyChange(PlayerGuiEvent.BIG_BLIND_PLAYER.toString(), null, bigBlindPlayer);
			} catch (NotRegisteredPlayerException e) {
				e.printStackTrace();
			}
			
			return true;
		}

		@Override
		public boolean onPlayRequest(PlayRequest request, ACLMessage aclMessage){

			playRequestMessage = aclMessage;
			
			System.out.println("[HPA] Player " + game.getPlayersContainer().getPlayerByAID(getAID()).getNickname() + " was asked to play.");
			
			Player me = game.getPlayersContainer().getPlayerByAID(getAID());

			PlayRequestEventData eventData = new PlayRequestEventData();
			eventData.addAllAvailableActions();

			if(game.getBetContainer().getCurrentBetAmount() > 0){
				eventData.removeAvailableAction(BetType.CHECK);
			}

			int currentBetAmount = TokenSetValueEvaluator.evaluateTokenSetValue(game.getBetContainer().getTokenValueDefinition(), me.getTokens());

			int minimumTokenValue = game.getBetContainer().getTokenValueDefinition().getValueForTokenType(TokenType.WHITE);
			
			// The minimum bet amount is either equal to current bet, 
			// or to the smallest token value if the current bet is equal to 0, 
			// or to the player bankroll if the the current bet is >= to the player's bankroll
			int minimumBetAmount = 0;
			if(currentBetAmount <= game.getBetContainer().getCurrentBetAmount()){
				minimumBetAmount = currentBetAmount;

				// Force the player to go all in if the current bet is >= to its bankroll (Can also fold)
				eventData.clearAvailableActions();
				eventData.addAvailableAction(BetType.ALL_IN);
				eventData.addAvailableAction(BetType.FOLD);
			}
			else if(game.getBetContainer().getCurrentBetAmount() == 0){
				minimumBetAmount = minimumTokenValue;
				
				eventData.removeAvailableAction(BetType.CALL);
				eventData.removeAvailableAction(BetType.RAISE);
			}
			else {
				minimumBetAmount = game.getBetContainer().getCurrentBetAmount();
			}
			eventData.setMinimumBetAmount(minimumBetAmount);

			// The maximum bet amount is equal to the bankroll of the player
			eventData.setMaximumBetAmount(game.getPlayersContainer().getPlayerByAID(getAID()).getBankroll(game.getBetContainer().getTokenValueDefinition()));

			eventData.setRaiseAmount(minimumBetAmount * 2);
			
			eventData.setErrorMessage(request.getErrorMessage());
			eventData.setRequestResentFollowedToError(request.isRequestResentFollowedToError());

			changes_game.firePropertyChange(PlayerGuiEvent.PLAY_REQUEST.toString(), null, eventData);
			
			return true;
		}
		
		@Override
		public boolean onPlayerFoldedRequest(PlayerFoldedRequest playerFoldedRequest, ACLMessage aclMsg) {
			
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
			this.addBehaviour(new TransactionBehaviour(this, new PlayerSubscriptionRequest(pseudo), simulation, ACLMessage.SUBSCRIBE));
		}
		// Faire un behaviour comme dans la simulation
		else if(arg0.getType() == WaitGameGuiEvent.GAME_START.ordinal())
		{
			wait_game_window.setVisible(false);
		}

		/**************************************
		 *  IHM Player
		 */
		if(arg0.getType() == PlayerGuiEvent.IHM_READY.ordinal())
		{
			System.out.println("IHM Ready");
			wait_game_window.setVisible(true);
			changes_game.firePropertyChange(PlayerGuiEvent.SHOW_IHM.toString(), null, null);
		}
		
		if(arg0.getType() == PlayerGuiEvent.PLAYER_BET.ordinal()) {
			
			if(playRequestMessage != null) {
				int betAmount = (int)((double)arg0.getParameter(0));
				
				System.out.println("[HPA] Player " + game.getPlayersContainer().getPlayerByAID(getAID()).getNickname() + " wants to bet: " + betAmount);
				
				replyToSimulationPlayRequest(betAmount);
			}			
		}
		
		else if(arg0.getType() == PlayerGuiEvent.PLAYER_CALLED.ordinal()) {
			
			if(playRequestMessage != null) {
				
				int betAmount = game.getBetContainer().getCurrentBetAmount();
				
				System.out.println("[HPA] Player " + game.getPlayersContainer().getPlayerByAID(getAID()).getNickname() + " wants to call (call amount: " + betAmount + ").");
				
				replyToSimulationPlayRequest(betAmount);
			}
		}

		else if(arg0.getType() == PlayerGuiEvent.PLAYER_FOLDED.ordinal()) {
			
			if(playRequestMessage != null) {
				System.out.println("[HPA] Player " + game.getPlayersContainer().getPlayerByAID(getAID()).getNickname() + " wants to fold.");	
				
				//Answering to simulation play request
				AgentHelper.sendReply(this, playRequestMessage, ACLMessage.REQUEST, new FoldRequest());
				
				//Setting play request to null, waiting for a new request from simulation
				playRequestMessage = null;
			}
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
}
