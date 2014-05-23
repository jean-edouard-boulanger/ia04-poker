package model.token.exception;

public class InvalidTokenAmountException extends Exception {
	public InvalidTokenAmountException(){
		super("An invalid amount of token has been set");
	}
	
	public InvalidTokenAmountException(int amount){
		super("An invalid amount of token has been set: " + amount);
	}
}
