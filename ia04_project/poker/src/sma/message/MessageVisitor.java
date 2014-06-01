package sma.message;

/**
 * Pattern visitor implementation, this base class should have an
 * handler method for each type of Jade message.
 * 
 * eg. public boolean OnStateChanged(OnStateChangedMessage msg){return false}
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
	public boolean onPlayerSubscriptionRequest(PlayerSubscriptionRequest request){return false;}
	public boolean onFailureMessage(FailureMessage msg) {return false;}
}
