package poker.game.operations.exception;

public class OperationFailureException extends Exception {
	public OperationFailureException(){
		super("The current operation failed");
	}	
}
