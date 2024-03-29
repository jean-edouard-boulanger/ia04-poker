package sma.message;

import jade.lang.acl.ACLMessage;
import sma.message.bet.request.AreBetsClosedRequest;
import sma.message.bet.request.BetRequest;
import sma.message.bet.request.DistributePotToWinnersRequest;
import sma.message.bet.request.DoesPlayerHaveToBetRequest;
import sma.message.bet.request.FoldRequest;
import sma.message.bet.request.MergeBetsRequest;
import sma.message.blind.request.RefreshBlindValueDefinitionRequest;
import sma.message.blind.request.ResetBlindRequest;
import sma.message.dealer.request.DealRequest;
import sma.message.determine_winner.DetermineWinnerRequest;
import sma.message.determine_winner.WinnerDeterminedResponse;
import sma.message.environment.notification.BetNotification;
import sma.message.environment.notification.BetsMergedNotification;
import sma.message.environment.notification.BlindValueDefinitionChangedNotification;
import sma.message.environment.notification.CardAddedToCommunityCardsNotification;
import sma.message.environment.notification.CardsEmptiedNotification;
import sma.message.environment.notification.CurrentPlayerChangedNotification;
import sma.message.environment.notification.DealerChangedNotification;
import sma.message.environment.notification.PlayerCardsRevealedNotification;
import sma.message.environment.notification.PlayerCheckNotification;
import sma.message.environment.notification.PlayerReceivedCardNotification;
import sma.message.environment.notification.PlayerReceivedTokenSetNotification;
import sma.message.environment.notification.PlayerReceivedUnknownCardNotification;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.notification.PlayerStatusChangedNotification;
import sma.message.environment.notification.PotEmptiedNotification;
import sma.message.environment.notification.TokenSetSentFromPotToPlayerNotification;
import sma.message.environment.notification.TokenValueDefinitionChangedNotification;
import sma.message.environment.notification.WinnerDeterminedNotification;
import sma.message.environment.request.AddCommunityCardRequest;
import sma.message.environment.request.AddPlayerTableRequest;
import sma.message.environment.request.BlindValueDefinitionChangeRequest;
import sma.message.environment.request.ChangePlayerStatusRequest;
import sma.message.environment.request.CurrentPlayerChangeRequest;
import sma.message.environment.request.DealCardToPlayerRequest;
import sma.message.environment.request.EmptyCardsRequest;
import sma.message.environment.request.EmptyPotRequest;
import sma.message.environment.request.GiveTokenSetToPlayerRequest;
import sma.message.environment.request.PlayerBetRequest;
import sma.message.environment.request.PlayerFoldedRequest;
import sma.message.environment.request.RevealPlayerCardsRequest;
import sma.message.environment.request.SendTokenSetToPlayerFromPotRequest;
import sma.message.environment.request.SetDealerRequest;
import sma.message.environment.request.SetTokenValueDefinitionRequest;
import sma.message.environment.request.ShowPlayerCardsRequest;
import sma.message.simulation.request.PlayRequest;

/**
 * Pattern visitor implementation, this base class should have an
 * handler method for each type of Jade message.
 * 
 * eg. public boolean OnStateChanged(OnStateChangedMessage msg, ACLMessage aclMsg){return false}
 * 
 * Theses handler are called by accept methods of Message classes.
 * The handler function should return false if the message is not accepted.
 * (the caller function could then put the message back in the message stack).
 * 
 * Remark: changing this class to an interface would make its usage easier (for instance, it could
 * then be implemented directly by agent or behavior classes) however that would require the usage of
 * defaulted interface methods which are only supported in java 8.
 */
public class MessageVisitor {
	
	public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {return false;}
	public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {return false;}
	public boolean onBooleanMessage(BooleanMessage message, ACLMessage aclMsg) {return false;}
	
	// Simulation
	public boolean onPlayerSubscriptionRequest(PlayerSubscriptionRequest request, ACLMessage aclMsg){return false;}
	
	// Dealer
	public boolean onDealRequest(DealRequest request, ACLMessage aclMsg){return false;}
	
	// Blind Management
	public boolean onResetBlindRequest(ResetBlindRequest request, ACLMessage aclMsg) {return false;}
	public boolean onRefreshBlindValueDefinitionRequest(RefreshBlindValueDefinitionRequest request, ACLMessage aclMsg) {return false;}
	
	// Determine winner
	public boolean onDetermineWinnerRequest(DetermineWinnerRequest request, ACLMessage aclMsg) {return false;}
	public boolean onWinnerDeterminedResponse(WinnerDeterminedResponse winnerDeterminedResponse, ACLMessage aclMsg) {return false;}
	
	// Bet
	public boolean onBetRequest(BetRequest request, ACLMessage aclMsg) {return false;}
	public boolean onAreBetsClosedRequest(AreBetsClosedRequest request, ACLMessage aclMsg) {return false;}
	public boolean onFoldRequest(FoldRequest request, ACLMessage aclMsg){return false;}
	public boolean onMergeBetsRequest(MergeBetsRequest request, ACLMessage aclMsg) {return false;}
	public boolean onBetsMergedNotification(BetsMergedNotification notification, ACLMessage aclMsg) {return false;}
	public boolean onDoesPlayerHaveToBetRequest(DoesPlayerHaveToBetRequest request, ACLMessage aclMessage){return false;}
	public boolean onDistributePotToWinnersRequest(DistributePotToWinnersRequest request, ACLMessage aclMsg) {return false;}
	
	// Environment
	public boolean onAddPlayerTableRequest(AddPlayerTableRequest notif, ACLMessage aclMsg){return false;}
	public boolean onAddCommunityCardRequest(AddCommunityCardRequest notif, ACLMessage aclMsg) {return false;}
	public boolean onDealCardToPlayerRequest(DealCardToPlayerRequest notif, ACLMessage aclMsg){return false;}
	public boolean onCurrentPlayerChangeRequest(CurrentPlayerChangeRequest notif, ACLMessage aclMsg){return false;}
	public boolean onEmptyCardsRequest(EmptyCardsRequest notif, ACLMessage aclMsg) {return false;}
	public boolean onGiveTokenSetToPlayerRequest(GiveTokenSetToPlayerRequest notif, ACLMessage aclMsg) {return false;}
	public boolean onBlindValueDefinitionChangeRequest(BlindValueDefinitionChangeRequest notif, ACLMessage aclMsg) {return false;}
	public boolean onSetTokenValueDefinitionRequest(SetTokenValueDefinitionRequest notif,	ACLMessage aclMsg) {return false;}
	public boolean onSetDealerRequest(SetDealerRequest setDealerRequest,ACLMessage aclMsg) {return false;}
	public boolean onShowPlayerCardsRequest(ShowPlayerCardsRequest showPlayerRequest, ACLMessage aclMsg) {return false;}
	public boolean onPlayerBetRequest(PlayerBetRequest playerBetRequest, ACLMessage aclMsg) { return false; }
	public boolean onPlayerFoldedRequest(PlayerFoldedRequest playerFoldedRequest, ACLMessage aclMsg) { return false; }
	public boolean onChangePlayerStatusRequest(ChangePlayerStatusRequest request, ACLMessage aclMsg){ return false; }
	public boolean onRevealPlayerCardsRequest(RevealPlayerCardsRequest request, ACLMessage aclMsg){ return false; }
	public boolean onSendTokenSetToPlayerFromPotRequest(SendTokenSetToPlayerFromPotRequest request, ACLMessage aclMsg){ return false; }
	public boolean onEmptyPotRequest(EmptyPotRequest emptyPotRequest, ACLMessage aclMsg) { return false; }
	
	// Environment notification 
	public boolean onEnvironmentChanged(Message notif, ACLMessage aclMsg){return false;}
	
	public boolean onSubscriptionOK(SubscriptionOKMessage notif, ACLMessage aclMsg){return false;}
	public boolean onPlayerReceivedUnknownCardNotification(PlayerReceivedUnknownCardNotification notification, ACLMessage aclMsg) {return false;}
	public boolean onPlayerReceivedCardNotification(PlayerReceivedCardNotification notification, ACLMessage aclMsg){return false;}
	public boolean onCardAddedToCommunityCardsNotification(CardAddedToCommunityCardsNotification notification, ACLMessage aclMsg){return false;}
	public boolean onCardsEmptiedNotification(CardsEmptiedNotification notification, ACLMessage aclMsg){return false;}
	public boolean onPlayerSitOnTableNotification(PlayerSitOnTableNotification notification, ACLMessage aclMsg){return false;}
	public boolean onPlayerReceivedTokenSetNotification(PlayerReceivedTokenSetNotification notification, ACLMessage aclMsg){return false;}
	public boolean onBetNotification(BetNotification notification, ACLMessage aclMsg){return false;}
	public boolean onPlayerCheckNotification(PlayerCheckNotification notification, ACLMessage aclMsg){return false;}
	public boolean onBlindValueDefinitionChangedNotification(BlindValueDefinitionChangedNotification notification, ACLMessage aclMsg){return false;}
	public boolean onCurrentPlayerChangedNotification(CurrentPlayerChangedNotification notification, ACLMessage aclMsg){return false;}
	public boolean onTokenValueDefinitionChangedNotification(TokenValueDefinitionChangedNotification notif, ACLMessage aclMsg) {return false;}
	public boolean onDealerChangedNotification(DealerChangedNotification dealerChangedNotification, ACLMessage aclMsg) {return false;}
	public boolean onPlayerCardsRevealedNotification(PlayerCardsRevealedNotification playerCardsRevealed, ACLMessage aclMsg) {return false;}
	public boolean onPlayerStatusChangedNotification(PlayerStatusChangedNotification notification, ACLMessage aclMsg) {return false;}
	public boolean onTokenSetSentFromPotToPlayerNotification(TokenSetSentFromPotToPlayerNotification request, ACLMessage aclMsg){ return false; }
	public boolean onPotEmptiedNotification(PotEmptiedNotification potEmptiedNotification, ACLMessage aclMsg) {return false;}
	public boolean onWinnerDeterminedNotification(WinnerDeterminedNotification notification, ACLMessage aclMsg) {return false;}
	
	//Simulation
	public boolean onPlayRequest(PlayRequest request, ACLMessage aclMessage){return false;}
	
}
