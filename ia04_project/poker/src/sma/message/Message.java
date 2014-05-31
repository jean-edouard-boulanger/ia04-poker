package sma.message;

public abstract class Message {
	public abstract boolean accept(MessageVisitor visitor);
	
	public static Message fromJson(String JSONSerializedMessage){
		//TODO
		return null;
	}
}
