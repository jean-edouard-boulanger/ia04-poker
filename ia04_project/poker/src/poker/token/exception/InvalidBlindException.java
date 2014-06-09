package poker.token.exception;

public class InvalidBlindException extends Exception {
	public InvalidBlindException(){
		super("Invalid blind given.");
	}
}
