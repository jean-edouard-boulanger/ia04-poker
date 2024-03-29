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
import java.util.HashMap;
import java.util.Map;

import javafx.embed.swing.JFXPanel;

import javax.swing.SwingUtilities;

import poker.card.exception.CommunityCardsFullException;
import poker.card.exception.UserDeckFullException;
import poker.card.heuristics.combination.model.Hand;
import poker.game.exception.NoPlaceAvailableException;
import poker.game.exception.NotRegisteredPlayerException;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.model.BetType;
import poker.game.model.Game;
import poker.game.player.model.Player;
import poker.game.player.model.PlayerStatus;
import poker.game.player.model.WinnerPlayer;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.helpers.TokenSetValueEvaluator;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBehaviour;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.PlayerSubscriptionRequest;
import sma.message.SubscriptionOKMessage;
import sma.message.bet.request.BetRequest;
import sma.message.bet.request.FoldRequest;
import sma.message.environment.notification.BetNotification;
import sma.message.environment.notification.BetsMergedNotification;
import sma.message.environment.notification.BlindValueDefinitionChangedNotification;
import sma.message.environment.notification.CardAddedToCommunityCardsNotification;
import sma.message.environment.notification.CardsEmptiedNotification;
import sma.message.environment.notification.CurrentPlayerChangedNotification;
import sma.message.environment.notification.DealerChangedNotification;
import sma.message.environment.notification.PlayerReceivedCardNotification;
import sma.message.environment.notification.PlayerReceivedTokenSetNotification;
import sma.message.environment.notification.PlayerReceivedUnknownCardNotification;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.notification.PlayerStatusChangedNotification;
import sma.message.environment.notification.PotEmptiedNotification;
import sma.message.environment.notification.TokenSetSentFromPotToPlayerNotification;
import sma.message.environment.notification.TokenValueDefinitionChangedNotification;
import sma.message.environment.notification.WinnerDeterminedNotification;
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

			Player player = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID());
			
			try {
				player.getDeck().addCard(notification.getReceivedCard());
			} catch (UserDeckFullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
		public boolean onCardsEmptiedNotification(CardsEmptiedNotification notification, ACLMessage aclMsg) {

			game.getCommunityCards().popCards();
			
			for(Player p : game.getPlayersContainer().getPlayers()) {
				p.getDeck().removeCards();
			}
			
			changes_game.firePropertyChange(PlayerGuiEvent.EMPTY_COMMUNITY_CARD.toString(), null, null);

			return true;
		}

		@Override
		public boolean onPlayerStatusChangedNotification(PlayerStatusChangedNotification notification, ACLMessage aclMsg) {
			
			Player player = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID());
			switch(notification.getNewStatus()){
				case FOLDED:
					changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_FOLDED.toString(), null, player.getTablePositionIndex());
					break;
				case IN_GAME:
					changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_IN_GAME.toString(), null, player.getTablePositionIndex());
					break;
				case OUT:
					changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_OUT.toString(), null, player.getTablePositionIndex());
					break;
				default:
					break;
			}
			
			System.out.println("[HPA] Player " + game.getPlayersContainer().getPlayerByAID(getAID()).getNickname() + " with " + notification.getNewStatus() + " status changed.");
			
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
			
			try {
				TokenSet tokenSetToAddInPot = notification.getBetTokenSet();
				
				if(notification.getBetAmount() != TokenSetValueEvaluator.evaluateTokenSetValue(game.getBetContainer().getTokenValueDefinition(), notification.getBetTokenSet())){
					tokenSetToAddInPot = TokenSetValueEvaluator.tokenSetFromAmount(notification.getBetAmount(), game.getBetContainer().getTokenValueDefinition());
				}
				
				Player player = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID());
				player.setTokens(player.getTokens().substractTokenSet(notification.getBetTokenSet()));	
				
				game.getBetContainer().addTokenToPlayerBet(notification.getPlayerAID(), tokenSetToAddInPot);
				
			} catch (InvalidTokenAmountException e) {
				e.printStackTrace();
			}
			
			PlayerBetEventData eventData = new PlayerBetEventData();
			eventData.setTokenSetUsedForBet(notification.getBetTokenSet());
			eventData.setPlayerIndex(game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID()).getTablePositionIndex());
			eventData.setBetAmount(game.getBetContainer().getCurrentBetAmount());
			eventData.setAmountAddedForBet(notification.getBetAmount());
			
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
		public boolean onBlindValueDefinitionChangedNotification(BlindValueDefinitionChangedNotification notification, ACLMessage aclMsg){

			game.setBlindValueDefinition(notification.getNewBlindValueDefinition());

			changes_game.firePropertyChange(PlayerGuiEvent.BLIND_VALUE.toString(), null, notification.getNewBlindValueDefinition());

			return true;
		}

		@Override
		public boolean onCurrentPlayerChangedNotification(CurrentPlayerChangedNotification notification, ACLMessage aclMsg){

			int playerIndex = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID()).getTablePositionIndex();
			changes_game.firePropertyChange(PlayerGuiEvent.CURRENT_PLAYER_CHANGED.toString(), null, game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID()));

			if(notification.getPlayerAID().equals(HumanPlayerAgent.this.getAID()))
				changes_game.firePropertyChange(PlayerGuiEvent.YOUR_TURN.toString(), null, null);
			
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
			
			//TEST
			int globalCurrentBetAmount = game.getBetContainer().getCurrentBetAmount();
			int playerCurrentBetAmount = TokenSetValueEvaluator.evaluateTokenSetValue(game.getBetContainer().getTokenValueDefinition(), game.getBetContainer().getPlayerCurrentBet(me));
			
			int minimumTokenValue = game.getBetContainer().getTokenValueDefinition().getValueForTokenType(TokenType.WHITE);
			
			int minimumBetAmount = 0;
			int maximumBetAmount = 0;
			int callAmount = 0;
			int playerBankroll = me.getBankroll(game.getBetContainer().getTokenValueDefinition()) + playerCurrentBetAmount;
			
			//If no one bet, can't fold or call
			if(globalCurrentBetAmount == 0) {
				eventData.removeAvailableAction(BetType.CALL);
				eventData.removeAvailableAction(BetType.FOLD);
			}
			else {
				eventData.removeAvailableAction(BetType.CHECK);
			}
			
			if(playerBankroll < globalCurrentBetAmount) {
				eventData.removeAvailableAction(BetType.CALL);
				eventData.removeAvailableAction(BetType.CHECK);
			}
			
			//Bet values
			if(globalCurrentBetAmount == 0) {
				minimumBetAmount = 2 * minimumTokenValue;
			}
			else {
				minimumBetAmount = 2 * globalCurrentBetAmount;
				callAmount = globalCurrentBetAmount;
			}
			
			if(playerBankroll < globalCurrentBetAmount || playerBankroll < minimumBetAmount) {
				minimumBetAmount = playerBankroll;
			}
			
			maximumBetAmount = playerBankroll;
			
/**
			//Bet amount for the current round
			int globalCurrentBetAmount = game.getBetContainer().getCurrentBetAmount();
			
			if(globalCurrentBetAmount > 0){
				//Can't check if someone has already bet
				eventData.removeAvailableAction(BetType.CHECK);
			}
			else {
				eventData.setCallAmount(0);
				eventData.removeAvailableAction(BetType.CHECK);
			}
			
			//Calculating player's current amount for the current round
			int playerCurrentBetAmount = TokenSetValueEvaluator.evaluateTokenSetValue(game.getBetContainer().getTokenValueDefinition(), game.getBetContainer().getPlayerCurrentBet(me));

			int minimumTokenValue = game.getBetContainer().getTokenValueDefinition().getValueForTokenType(TokenType.WHITE);
			
			int minimumBetAmount = 0;
			
			if(playerCurrentBetAmount <= globalCurrentBetAmount){
				
				if(globalCurrentBetAmount == 0) {
					minimumBetAmount = 2 * minimumTokenValue;
				}
				else
					minimumBetAmount = 2 * globalCurrentBetAmount;
					
				eventData.setCallAmount(globalCurrentBetAmount);
				eventData.addAvailableAction(BetType.FOLD);
			}
			else if(globalCurrentBetAmount == 0){
				minimumBetAmount = minimumTokenValue;
				
				eventData.removeAvailableAction(BetType.CALL);
				eventData.removeAvailableAction(BetType.FOLD);
			}

			int playerBankroll = me.getBankroll(game.getBetContainer().getTokenValueDefinition());
			
			if(playerBankroll < eventData.getCallAmount()) {
				minimumBetAmount = playerBankroll;
				eventData.clearAvailableActions();
				eventData.addAvailableAction(BetType.FOLD);
				eventData.addAvailableAction(BetType.RAISE);
			}
		**/	
			eventData.setMinimumBetAmount(minimumBetAmount);

			// The maximum bet amount is equal to the bankroll of the player
			eventData.setMaximumBetAmount(maximumBetAmount);
			eventData.setCallAmount(callAmount);
			
			eventData.setErrorMessage(request.getErrorMessage());
			eventData.setRequestResentFollowedToError(request.isRequestResentFollowedToError());

			changes_game.firePropertyChange(PlayerGuiEvent.PLAY_REQUEST.toString(), null, eventData);
			
			return true;
		}
		
		@Override
		public boolean onPlayerFoldedRequest(PlayerFoldedRequest playerFoldedRequest, ACLMessage aclMsg) {
			
			Player player = game.getPlayersContainer().getPlayerByAID(playerFoldedRequest.getPlayerAID());
			
			player.setStatus(PlayerStatus.FOLDED);
			
			System.out.println("[HPA] Player " + player.getNickname() + " folded.");
			
			return true;
		}
		
		@Override
		public boolean onBetsMergedNotification(BetsMergedNotification notification, ACLMessage aclMsg) {
			
			System.out.println("[" + getLocalName() + "] Transferred current bets to pot.");
			
			game.getBetContainer().transferCurrentBetsToPot();
			changes_game.firePropertyChange(PlayerGuiEvent.RESET_PLAYER_BETS.toString(), null, null);

			return true;
		}
		
		@Override
		public boolean onWinnerDeterminedNotification(WinnerDeterminedNotification notification, ACLMessage aclMsg) {
			
			Map<Player, Hand> handPlayerWinners = new HashMap<Player, Hand>();
			
			System.out.println("[HPA] Player winner");
			
			for(WinnerPlayer winner : notification.getWinners())
			{
				handPlayerWinners.put(game.getPlayersContainer().getPlayerByAID(winner.getPlayerAID()), winner.getWinningHand());
			}
			
			System.out.println("[HPA] " + handPlayerWinners.size());
			
			changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_WINNER.toString(), null, handPlayerWinners);
			return true;
		}
		
		@Override
		public boolean onPotEmptiedNotification(PotEmptiedNotification emptyPotRequest, ACLMessage aclMsg) {
			System.out.println("DEBUG [HBA] Pot cleared");
			game.getBetContainer().clearPot();
			changes_game.firePropertyChange(PlayerGuiEvent.CLEAR_POT.toString(), null, null);
			return true;
		}
		
		@Override
		public boolean onTokenSetSentFromPotToPlayerNotification(TokenSetSentFromPotToPlayerNotification notification, ACLMessage aclMsg) {
			Player player = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID());
			player.setTokens(player.getTokens().addTokenSet(notification.getSentTokenSet()));

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
			
	//		int playerCurrentBetAmount = TokenSetValueEvaluator.evaluateTokenSetValue(game.getBetContainer().getTokenValueDefinition(), game.getBetContainer().getPlayerCurrentBet(game.getPlayersContainer().getPlayerByAID(getAID())));

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
				
				changes_game.firePropertyChange(PlayerGuiEvent.PLAYER_CANT_PLAY.toString(), null, null);

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
