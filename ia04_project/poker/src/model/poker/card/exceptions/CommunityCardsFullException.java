package model.poker.card.exceptions;

public class CommunityCardsFullException extends Exception {
	public CommunityCardsFullException(){
		super("Tried to push a card on the community cards while it was already full");
	}
}
