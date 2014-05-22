package classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Contains the cards of the game
 *
 */
public class Deck {
	
	protected static Deck instance = null;
	protected ArrayList<Card> cards;
	
	public static Deck instance() {		
		if (instance == null) {
			instance = new Deck();
			instance.resetDeck();
		}

		return instance;
	}

	protected Deck() {

	}
	
	public ArrayList<Card> getCards() {
		return cards;
	}

	private void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}
	
	public void resetDeck() {
		this.cards = new ArrayList<Card>();
		
		for(CardSuit suit : CardSuit.values()) {
			for(CardRank rank : CardRank.values()) {
				Card card = new Card(rank, suit);
				this.cards.add(card);
			}
		}
		
		Collections.shuffle(cards);
	}
	
	public Card getNextCard() {
		int count = cards.size();
		
		Card card = cards.get(count - 1);
		
		cards.remove(count - 1);
		
		return card;
	}
	
	public void removeNextCard() {
		int count = cards.size();
				
		cards.remove(count - 1);
	}
	
	public void mixCards() {
		Collections.shuffle(cards);
	}
	
	public void printDeck() {
		for (int i = 0; i < cards.size(); i++) {
			System.out.println(cards.get(i));
		}
	}
}
