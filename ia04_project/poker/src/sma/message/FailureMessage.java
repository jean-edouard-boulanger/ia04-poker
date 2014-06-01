package sma.message;

public class FailureMessage extends Message {
	
	private String message;
	
	public FailureMessage(String message){
		this.message = message;  
	}
	
	public FailureMessage(){
		this.message = "";  
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean accept(MessageVisitor visitor){
		return visitor.onFailureMessage(this);
	}
	
}
