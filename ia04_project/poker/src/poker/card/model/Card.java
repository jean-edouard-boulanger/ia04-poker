package poker.card.model;

import java.util.Comparator;

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
	
	public int compareTo(Card card) {
		return this.rank.getCardRank() > card.getRank().getCardRank() ? 1 : this.rank.getCardRank() < card.getRank().getCardRank() ? -1 : 0;
	}
	
	public static class CardComparator implements Comparator<Card> {
		
		public CardComparator() {

		}
		
		@Override
		public int compare(Card o1, Card o2) {			
			return o1.compareTo(o2);
		}
	}
}
