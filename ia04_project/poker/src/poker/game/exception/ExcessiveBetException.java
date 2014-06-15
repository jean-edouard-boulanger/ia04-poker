package poker.game.exception;

public class ExcessiveBetException extends Exception {
	public ExcessiveBetException() {
		super("Tried to place an excessibe net");
	}
}
