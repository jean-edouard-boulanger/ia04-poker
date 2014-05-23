package model.poker.card.exception;

public class UserDeckFullException extends Exception {
	public UserDeckFullException(){
		super("Tries to add a card on the user deck while it was already full");
	}
}
