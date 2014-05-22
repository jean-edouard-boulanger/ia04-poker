package classes;

/**
 * Contains the cards of a player
 *
 */
public class CardSet {
	
	Card card1;
	Card card2;
	
	public CardSet() {
		
	}
	
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
	
	public void addCard(Card newCard) {
		if(card1 != null && card2 != null)
		{
			System.out.println("Player has already two cards.");
		}
		
		if(card1 == null) {
			card1 = newCard;
		}
		else {
			card2 = newCard;
		}
	}
}
