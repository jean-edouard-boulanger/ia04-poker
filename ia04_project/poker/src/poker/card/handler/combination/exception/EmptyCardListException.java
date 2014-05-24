package poker.card.handler.combination.exception;

public class EmptyCardListException extends Exception {
	public EmptyCardListException(){
		super("Tried to get the best combination of empty card list.");
	}
}
