package poker.card.heuristics.combination.model;

import java.util.ArrayList;

import poker.card.model.Card;

public class Hand {
	Combination combination;
	ArrayList<Card> cards;
	
	public Hand(Combination combination, ArrayList<Card> cards) {
		this.combination = combination;
		this.cards = cards;
	}
	
	public Hand() {

	}

	public Combination getCombination() {
		return combination;
	}

	public void setCombination(Combination combination) {
		this.combination = combination;
	}

	public ArrayList<Card> getCards() {
		return cards;
	}

	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}
	
	public String toString() {
		String hand =
				"Hand: \n"
				+ "Combination: " + getCombination() + "\n"
				+ "Cards: [";
		
		for(Card c : cards) {
			hand += c.toString();
			
			if(c != cards.get(cards.size() - 1))
				hand += ", ";
		}
		
		hand += "]\n---------";
		
		return hand;
	}
}
