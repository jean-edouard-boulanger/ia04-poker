package poker.token.exception;

public class InvalidRepartitionException extends Exception {
	public InvalidRepartitionException(){
		super("The provided repartition is not valid");
	}
}
