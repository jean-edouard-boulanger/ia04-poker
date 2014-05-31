package sma.message;

/**
 * Pattern visitor implementation, this baseClasse should have an
 * handler method for each type of Jade message.
 * 	eg. false OnStateChanged(OnStateChangedMessage msg){return false}
 * Theses handler are called by accept methods of Message classes.
 * The handler function should return false if the messaged is not accepted.
 * (the caller function could then put the message back in the stack).
 */
public class MessageVisitor {
	
}
