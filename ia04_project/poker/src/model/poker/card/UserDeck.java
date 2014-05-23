package model.poker.card;

import model.poker.card.exceptions.UserDeckFullException;

/**
 * Contains the cards of a player
 *
 */
public class UserDeck {
	
	private Card card1;
	private Card card2;
	
	public UserDeck() {}
	
	public Card getCard1() {
		return card1;
	}
	public void setCard1(Card card1) {
		this.card1 = card1;
	}
	public Card getCard2() {
		return card2;
	}
	public void setCard2(Card card2) {
		this.card2 = card2;
	}

	public void removeCards() {
		card1 = null;
		card2 = null;
	}
	
	public void addCard(Card newCard) throws UserDeckFullException {
		if(card1 != null && card2 != null)
		{
			throw new UserDeckFullException();
		}
		
		if(card1 == null) {
			card1 = newCard;
		}
		else {
			card2 = newCard;
		}
	}
}
