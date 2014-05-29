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
	
	private static Card extremityCardInList(ArrayList<Card> cards, boolean highest) throws EmptyCardListException {
		if(cards == null || cards.size() == 0) {
			throw new EmptyCardListException();
		}

		Card card1 = cards.get(0);
		
		for (int i = 1; i < cards.size(); i++) {
			Card card2 = cards.get(i);
			
			if(card1.compareTo(card2) == -1 && highest == true) {
				card1 = card2;
			}
			else if(card1.compareTo(card2) == 1 && highest == false) {
				card1 = card2;
			}
		}
		
		return card1;
	}
	
	/**
	 * 
	 * @param cards
	 * @return Highest card (card of highest rank) found in the list
	 * @throws EmptyCardListException 
	 */
	public static Card highestCardInList(ArrayList<Card> cards) throws EmptyCardListException {
		return extremityCardInList(cards, true);
	}
	
	/**
	 * 
	 * @param cards
	 * @return Lowest card (card of lowest rank) found in the list
	 * @throws EmptyCardListException 
	 */
	public static Card lowestCardInList(ArrayList<Card> cards) throws EmptyCardListException {		
		return extremityCardInList(cards, false);
	}
	
	public static CardRank getCombinationRank(Hand hand) throws EmptyCardListException {
		//if(combination == Combination.FLUSH || combination == Combination.FULL_HOUSE || combination == Combination.TWO_PAIR)
		
		switch (hand.getCombination()) {
		case HIGH_CARD:
		case ONE_PAIR:
		case THREE_OF_A_KIND:
		case FOUR_OF_A_KIND:
			return highestCardInList(hand.getCombinationCards()).getRank();
			
			//Returning lowest card because ace can be used at the beginning or at the end of these combinations. If it is at the beginning, the function will return 2
		case STRAIGHT:
		case STRAIGHT_FLUSH:
			return lowestCardInList(hand.getCombinationCards()).getRank();
			
		default:
			break;
		}
		
		return highestCardInList(hand.getCombinationCards()).getRank();
	}
}
