package poker.card.heuristics.probability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import poker.card.model.Card;
import poker.card.model.CardDeck;
import poker.card.model.CardRank;
import poker.card.model.CardSuit;
import poker.card.model.UserDeck;

public class RandomCardDeckGenerator {

	private Stack<Card> cards = null;
	private ArrayList<Card> excludedCards;

	public RandomCardDeckGenerator(){
		this.excludedCards = new ArrayList<Card>();
	}
	
	public RandomCardDeckGenerator(ArrayList<Card> excludedCards){
		if(excludedCards == null){
			this.excludedCards = new ArrayList<Card>();
		}
		else{
			this.excludedCards = excludedCards;
		}
	}
	
	public RandomCardDeckGenerator(UserDeck d){
		this.excludedCards = d.getCards();
	}
		
	public CardDeck GenerateNext(){
		if(this.cards == null){
			this.initializeCards();
		}
		Collections.shuffle(cards);
		
		Stack<Card> tmpCards = new Stack<Card>();
		tmpCards.addAll(this.cards);
		
		return new CardDeck(tmpCards);
	}
	
	public void setExcludedCards(ArrayList<Card> excludedCards){
		this.cards = null;
		this.excludedCards = excludedCards;
	}
	
	public ArrayList<Card> getExcludedCards(){
		return this.excludedCards;
	}
	
	public Stack<Card> getCards(){
		return this.cards;
	}
	
	private void initializeCards(){
		this.cards = new Stack<Card>();
		for(CardSuit suit : CardSuit.values()) {
			for(CardRank rank : CardRank.values()) {
				Card card = new Card(rank, suit);
				if(!this.excludedCards.contains(card)){
					this.cards.push(card);
				}
			}
		}
	}
}
