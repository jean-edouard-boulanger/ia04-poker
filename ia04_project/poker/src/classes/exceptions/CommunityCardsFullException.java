package classes.exceptions;

public class CommunityCardsFullException extends Exception {
	public CommunityCardsFullException(){
		super("Tried to push a card on the community card while it was already full");
	}
}
