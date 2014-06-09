package poker.game.exception;

public class NoPlaceAvailableException extends Exception {
	
	public NoPlaceAvailableException(){
		super("No place available on the table");
	}
	
}
