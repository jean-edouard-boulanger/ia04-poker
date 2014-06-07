package sma.message;

import jade.lang.acl.ACLMessage;

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
}