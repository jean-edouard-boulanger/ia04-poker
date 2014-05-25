package poker.card.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class CardDeck {

	Stack<Card> cardDeck;
	
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
	
}