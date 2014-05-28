package poker.card.heuristics.combination.helper;

import java.util.ArrayList;
import java.util.Collections;

import poker.card.heuristics.combination.exception.EmptyCardListException;
import poker.card.heuristics.combination.model.Hand;
import poker.card.model.Card;
import poker.card.model.CardRank;

public class HandHelper {

	public static Card getHighestCardInHand(Hand h){
		ArrayList<Card> cards = new ArrayList<Card>(h.getCombinationCards());
		Collections.sort(cards, new Card.CardComparator());
		return cards.get(0);
	}
	
	/**
	 * 
	 * @param cards
	 * @return Highest card (card of highest rank) found in the list
	 * @throws EmptyCardListException 
	 */
	public static Card highestCardInList(ArrayList<Card> cards) throws EmptyCardListException {
		if(cards == null || cards.size() == 0) {
			throw new EmptyCardListException();
		}

		Card card1 = cards.get(0);
		
		for (int i = 1; i < cards.size(); i++) {
			Card card2 = cards.get(i);
			
			if(card1.compareTo(card2) == -1) {
				card1 = card2;
			}
		}
		
		return card1;
	}
	
	public static CardRank getCombinationRank(Hand hand) throws EmptyCardListException {
		//if(combination == Combination.FLUSH || combination == Combination.FULL_HOUSE || combination == Combination.TWO_PAIR)
		
		return highestCardInList(hand.getCombinationCards()).getRank();
	}
}
