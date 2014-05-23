package poker.card.model;

public class Card {
	private CardSuit suit;
	private CardRank rank;
		
	public Card(CardRank cardRank, CardSuit cardSuit) {		
		this.suit = cardSuit;
		this.rank = cardRank;
	}

	public Card() {
		
	}
	
	public CardSuit getSuit() {
		return suit;
	}

	public CardRank getRank() {
		return rank;
	}
	
	public String toString() {
		return "|" + rank + " of " + suit + "|";
	}

	private void setSuit(CardSuit suit) {
		this.suit = suit;
	}

	private void setRank(CardRank rank) {
		this.rank = rank;
	}
}
