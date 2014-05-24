package poker.card.helper;

import java.util.ArrayList;

import poker.card.model.Card;
import poker.card.model.CardDeck;


public class CardPickerHelper {

	public static ArrayList<Card> pickCardsFromDeck(CardDeck deck, int nbCards){
		ArrayList<Card> pickedCards = new ArrayList<Card>();
		for(int i=0; i < nbCards && !deck.isEmpty(); i++){
			pickedCards.add(deck.pickCard());
		}
		return pickedCards;
	}
	
	public static ArrayList<Card> pickCardsFromDeck(CardDeck deck, CustomPickSequence sequence){
		ArrayList<Card> pickedCards = new ArrayList<Card>();
		Card c = null;
		
		int nbCardsLookedUp = sequence.getNumberCardsLookedUp();
		for(int i = 1; i <= nbCardsLookedUp; i++){
			c = deck.pickCard();
			if(sequence.isCardPickedUp(i)){
				pickedCards.add(c);
			}
		}
		
		return pickedCards;
	}
	
}
