package sma.message;

import jade.lang.acl.ACLMessage;
import sma.message.environment.notification.BlindValueDefinitionChangedNotification;
import sma.message.environment.notification.CardAddedToCommunityCardsNotification;
import sma.message.environment.notification.CommunityCardsEmptiedNotification;
import sma.message.environment.notification.CurrentPlayerChangedNotification;
import sma.message.environment.notification.PlayerBetNotification;
import sma.message.environment.notification.PlayerCheckNotification;
import sma.message.environment.notification.PlayerFoldedNotification;
import sma.message.environment.notification.PlayerReceivedTokenSetNotification;
import sma.message.environment.notification.PlayerReceivedCardNotification;
import sma.message.environment.notification.PlayerReceivedUnknownCardNotification;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.request.AddCommunityCardRequest;
import sma.message.environment.request.AddPlayerTableRequest;
import sma.message.environment.request.ChangeBlindValueDefinitionRequest;
import sma.message.environment.request.CurrentPlayerChangeRequest;
import sma.message.environment.request.DealCardToPlayerRequest;
import sma.message.environment.request.EmptyCommunityCardsRequest;
import sma.message.environment.request.GiveTokenSetToPlayerRequest;

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
	
	public boolean onPlayerSubscriptionRequest(PlayerSubscriptionRequest request, ACLMessage aclMsg){return false;}
	public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {return false;}
	public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {return false;}
	

	public boolean onPlayerReceivedUnknownCardNotification(PlayerReceivedUnknownCardNotification notification, ACLMessage aclMsg) {return false;}
	public boolean onPlayerReceivedCardNotification(PlayerReceivedCardNotification notification, ACLMessage aclMsg){return false;}
	public boolean onCardAddedToCommunityCardsNotification(CardAddedToCommunityCardsNotification notification, ACLMessage aclMsg){return false;}
	public boolean onCommunityCardsEmptiedNotification(CommunityCardsEmptiedNotification notification, ACLMessage aclMsg){return false;}
	public boolean onPlayerFoldedNotification(PlayerFoldedNotification notification, ACLMessage aclMsg){return false;}
	public boolean onPlayerSitOnTableNotification(PlayerSitOnTableNotification notification, ACLMessage aclMsg){return false;}
	public boolean onPlayerReceivedTokenSetNotification(PlayerReceivedTokenSetNotification notification, ACLMessage aclMsg){return false;}
	public boolean onPlayerBetNotification(PlayerBetNotification notification, ACLMessage aclMsg){return false;}
	public boolean onPlayerCheckNotification(PlayerCheckNotification notification, ACLMessage aclMsg){return false;}
	public boolean onBlindValueDefinitionChangedNotification(BlindValueDefinitionChangedNotification notification, ACLMessage aclMsg){return false;}
	public boolean onCurrentPlayerChangedNotification(CurrentPlayerChangedNotification notification, ACLMessage aclMsg){return false;}
	
	public boolean onAddPlayerTableRequest(AddPlayerTableRequest request, ACLMessage aclMsg){return false;}
	public boolean onAddCommunityCardRequest(AddCommunityCardRequest request, ACLMessage aclMsg) {return false;}
	public boolean onDealCardToPlayerRequest(DealCardToPlayerRequest request, ACLMessage aclMsg){return false;}
	public boolean onCurrentPlayerChangeRequest(CurrentPlayerChangeRequest request, ACLMessage aclMsg){return false;}
	public boolean onEmptyCommunityCardsRequest(EmptyCommunityCardsRequest request, ACLMessage aclMsg) {return false;}
	public boolean onGiveTokenSetToPlayerRequest(GiveTokenSetToPlayerRequest request, ACLMessage aclMsg) {return false;}
	public boolean onChangeBlindValueDefinitionRequest(ChangeBlindValueDefinitionRequest request, ACLMessage aclMsg) {return false;}
	
}