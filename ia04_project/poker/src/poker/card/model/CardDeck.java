package poker.card.model;

import java.util.Collections;
import java.util.Stack;

public class CardDeck {

	private Stack<Card> cardDeck;
	
	public CardDeck(){
		this.cardDeck = new Stack<Card>();
	}
	
	public CardDeck(Stack<Card> cardDeck){
		this.cardDeck = cardDeck;
	}
	
	public void setCardDeck(Stack<Card> cardDeck){
		this.cardDeck = cardDeck;
	}
	
	public Stack<Card> getCardDeck(){
		return this.cardDeck;
	}
	
	public Card pickCard(){
		return this.cardDeck.pop();
	}
	
	public boolean isEmpty(){
		return this.cardDeck.isEmpty();
	}
	
	public void removeNextCard() {
		this.pickCard();
	}
	
	public void mixCards() {
		Collections.shuffle(cardDeck);
	}
	
	public void printDeck() {
	    for (Card card : cardDeck) {
		System.out.println(card);
	    }
	}
	
	/**
	 * @return a regular 52 card game deck, shuffled.
	 */
	static public CardDeck getNewRegularGameDeck() {
	    Stack<Card> cards = new Stack<Card>();
		
	    for(CardSuit suit : CardSuit.values()) {
		for(CardRank rank : CardRank.values()) {
		    cards.add(new Card(rank, suit));
		}
	    }
	    CardDeck deck = new CardDeck(cards);
	    deck.mixCards();
	    return deck;
	}		
}
