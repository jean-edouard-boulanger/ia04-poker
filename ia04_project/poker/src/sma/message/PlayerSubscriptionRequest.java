package sma.message;

public class PlayerSubscriptionRequest extends Message {
	
	
	
	public boolean accept(MessageVisitor visitor){
		return visitor.onPlayerSubscriptionRequest(this);
	}
	
}
