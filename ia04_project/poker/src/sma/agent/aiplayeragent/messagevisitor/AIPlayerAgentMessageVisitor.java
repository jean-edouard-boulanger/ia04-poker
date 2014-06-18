package sma.agent.aiplayeragent.messagevisitor;

import gui.player.PlayerWindow.PlayerGuiEvent;
import gui.player.event.model.PlayRequestEventData;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
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
import sma.agent.AIPlayerAgent;
import sma.message.MessageVisitor;
import sma.message.SubscriptionOKMessage;
import sma.message.bet.notification.BetsMergedNotification;
import sma.message.environment.notification.BetNotification;
import sma.message.environment.notification.BlindValueDefinitionChangedNotification;
import sma.message.environment.notification.CardAddedToCommunityCardsNotification;
import sma.message.environment.notification.CommunityCardsEmptiedNotification;
import sma.message.environment.notification.DealerChangedNotification;
import sma.message.environment.notification.PlayerReceivedCardNotification;
import sma.message.environment.notification.PlayerReceivedTokenSetNotification;
import sma.message.environment.notification.PlayerReceivedUnknownCardNotification;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.notification.PlayerStatusChangedNotification;
import sma.message.environment.notification.TokenValueDefinitionChangedNotification;
import sma.message.environment.request.PlayerFoldedRequest;
import sma.message.simulation.request.PlayRequest;

public class AIPlayerAgentMessageVisitor extends MessageVisitor {
	
	private Game game;
	AIPlayerAgent myAgent;
	
	public AIPlayerAgentMessageVisitor(Game game, AIPlayerAgent agent) {
		this.myAgent = agent;
		this.game = game;
	}
	
	@Override
	public boolean onPlayerReceivedUnknownCardNotification(PlayerReceivedUnknownCardNotification notification, ACLMessage aclMsg) {

		System.out.println("[HumanPlayerAgent] Unknown card notification received. PLAYER_RECEIVED_UNKNOWN_CARD fired");	

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

		return true;
	}

	@Override
	public boolean onCardAddedToCommunityCardsNotification(CardAddedToCommunityCardsNotification notification, ACLMessage aclMsg) {

		try {
			game.getCommunityCards().pushCard(notification.getNewCommunityCard());
		} catch (CommunityCardsFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean onCommunityCardsEmptiedNotification(CommunityCardsEmptiedNotification notification, ACLMessage aclMsg) {

		game.getCommunityCards().popCards();

		return true;
	}

	@Override
	public boolean onPlayerStatusChangedNotification(PlayerStatusChangedNotification notification, ACLMessage aclMsg) {
		
		Player player = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID());
		switch(notification.getNewStatus()){
			case FOLDED:
				player.setStatus(PlayerStatus.FOLDED);
			default:
				break;
		}
		
		System.out.println("[HPA] Player " + game.getPlayersContainer().getPlayerByAID(myAgent.getAID()).getNickname() + " with " + notification.getNewStatus() + " status changed.");
		
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

		return true;
	}

	@Override
	public boolean onPlayerReceivedTokenSetNotification(PlayerReceivedTokenSetNotification notification, ACLMessage aclMsg){

		Player player = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID());
		player.setTokens(player.getTokens().addTokenSet(notification.getReceivedTokenSet()));

		return true;
	}

	@Override
	public boolean onBetNotification(BetNotification notification, ACLMessage aclMsg){

		// Update la mise minimum pour relancer apr�s
		TokenSet betTokenSet = notification.getBetTokenSet();
					
		Player player = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID());
					
		game.getBetContainer().setPlayerCurrentBet(player.getAID(), TokenSetValueEvaluator.tokenSetFromAmount(notification.getBetAmount(), game.getBetContainer().getTokenValueDefinition()));
		
		try {
			player.setTokens(player.getTokens().substractTokenSet(betTokenSet));
		} catch (InvalidTokenAmountException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public boolean onBlindValueDefinitionChangedNotification(BlindValueDefinitionChangedNotification notification, ACLMessage aclMsg){

		game.setBlindValueDefinition(notification.getNewBlindValueDefinition());

		return true;
	}

	@Override
	public boolean onTokenValueDefinitionChangedNotification(TokenValueDefinitionChangedNotification notif, ACLMessage aclMsg) {

		game.getBetContainer().setTokenValueDefinition(notif.getTokenValueDefinition());

		return true;
	}

	@Override
	public boolean onSubscriptionOK(SubscriptionOKMessage notif, ACLMessage aclMsg){

		game = notif.getGame();
		System.out.println("Subscription OK.");

		return true;
	}

	@Override
	public boolean onDealerChangedNotification(DealerChangedNotification dealerChangedNotification, ACLMessage aclMsg) {

		try {
			game.getPlayersContainer().setDealer(dealerChangedNotification.getDealer());
		} catch (NotRegisteredPlayerException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public boolean onPlayRequest(PlayRequest request, ACLMessage aclMessage){

		myAgent.setPlayRequestMessage(aclMessage);
		
		System.out.println("[" + myAgent.getLocalName() + "] Player " + game.getPlayersContainer().getPlayerByAID(myAgent.getAID()).getNickname() + " was asked to play.");
		
		Player me = game.getPlayersContainer().getPlayerByAID(myAgent.getAID());

		PlayRequestEventData eventData = new PlayRequestEventData();
		eventData.addAllAvailableActions();

		//Bet amount for the current round
		int globalCurrentBetAmount = game.getBetContainer().getCurrentBetAmount();
		
		if(globalCurrentBetAmount > 0){
			//Can't check if someone has already bet
			eventData.removeAvailableAction(BetType.CHECK);
		}
		else {
			eventData.setCallAmount(0);
		}
		
		//Calculating player's current amount for the current round
		int playerCurrentBetAmount = TokenSetValueEvaluator.evaluateTokenSetValue(game.getBetContainer().getTokenValueDefinition(), game.getBetContainer().getPlayerCurrentBet(me));

		int minimumTokenValue = game.getBetContainer().getTokenValueDefinition().getValueForTokenType(TokenType.WHITE);
		
		int minimumBetAmount = 0;
		
		if(playerCurrentBetAmount <= globalCurrentBetAmount){
			
			if(globalCurrentBetAmount == 0)
				minimumBetAmount = 2 * minimumTokenValue;
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
		
		if(playerBankroll < minimumBetAmount) {
			minimumBetAmount = playerBankroll;
			eventData.clearAvailableActions();
			eventData.addAvailableAction(BetType.FOLD);
			eventData.addAvailableAction(BetType.RAISE);
		}
		
		eventData.setMinimumBetAmount(minimumBetAmount);

		// The maximum bet amount is equal to the bankroll of the player
		eventData.setMaximumBetAmount(playerBankroll);
		
		eventData.setErrorMessage(request.getErrorMessage());
		eventData.setRequestResentFollowedToError(request.isRequestResentFollowedToError());
		
		return true;
	}
	
	@Override
	public boolean onPlayerFoldedRequest(PlayerFoldedRequest playerFoldedRequest, ACLMessage aclMsg) {
		
		Player player = game.getPlayersContainer().getPlayerByAID(playerFoldedRequest.getPlayerAID());
		
		player.setStatus(PlayerStatus.FOLDED);
		
		System.out.println("[" + myAgent.getLocalName() + "] Player " + player.getNickname() + " folded.");
		
		return true;
	}
	
	@Override
	public boolean onBetsMergedNotification(BetsMergedNotification notification, ACLMessage aclMsg) {
		
		System.out.println("[" + myAgent.getLocalName() + "] Transferred current bets to pot.");
		
		game.getBetContainer().transferCurrentBetsToPot();

		return true;
	}
}
