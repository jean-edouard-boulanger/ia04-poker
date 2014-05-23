package model.token.exception;

public class InvalidTokenValueException extends Exception {
	public InvalidTokenValueException(){
		super("An invalid value has been set for a token");
	}
	
	public InvalidTokenValueException(int value){
		super("An invalid value has been set for a token: " + value);
	}
}
