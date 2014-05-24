package poker.game.management.exception;

public class ExcessiveBetException extends Exception {

	public ExcessiveBetException(){
		super("Bet amount cannot excess the user's bankroll");
	}
	
}
