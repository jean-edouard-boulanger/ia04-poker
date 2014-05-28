package poker.card.heuristics.combination.helper;

import java.util.ArrayList;
import java.util.ListIterator;

import poker.card.heuristics.combination.exception.EmptyCardListException;
import poker.card.heuristics.combination.model.Hand;
import poker.card.model.CardRank;

public class HandComparator {
	
	protected static ArrayList<Hand> handsWithHighestList(ArrayList<Hand> hands) {
		
		
		return null;
	}
	
	/**
	 * Algorithm works for 1, 2, 3, 4 identic 4 cards: take the first card of the combination cards list to compare them
	 * @param hands
	 * @return
	 * @throws EmptyCardListException 
	 */
	protected static ArrayList<Hand> bestIdenticCardsHand(ArrayList<Hand> hands) throws EmptyCardListException {		
		CardRank highestRank = HandHelper.getCombinationRank(hands.get(0));
		
		for(Hand h : hands) {
			if(highestRank.compareTo(HandHelper.getCombinationRank(h)) == -1) {
				highestRank = HandHelper.getCombinationRank(h);
			}
		}
		
		ListIterator<Hand> iter = hands.listIterator();
		
		while(iter.hasNext()){
		    if(HandHelper.getCombinationRank(iter.next()).compareTo(highestRank) == -1) {
		        iter.remove();
		    }
		}

		//Only hands with the same rank (highest rank in the list) are in the list
		if(hands.size() == 1)
			return hands;
		
		return handsWithHighestList(hands);
	}
	
	protected static ArrayList<Hand> compareHands(ArrayList<Hand> hands) {
		switch(hands.get(0).getCombination()) {
		case HIGH_CARD:
			return null;
		case ONE_PAIR:
			return null;
		case TWO_PAIR:
			return null;
		case THREE_OF_A_KIND:
			return null;
		case STRAIGHT:
			return null;
		case FLUSH:
			return null;
		case FULL_HOUSE:
			return null;
		case FOUR_OF_A_KIND:
			return null;
		case STRAIGHT_FLUSH:
			return null;
			
		default:
			return null;
		}
	}
	
	public static ArrayList<Hand> bestHand(ArrayList<Hand> hands) {
		if(hands == null || hands.size() == 0)
			return null;
		
		if(hands.size() == 1)
			return hands;
		
		Hand highestHand = hands.get(0);
		
		ArrayList<Hand> handsToCompare = (ArrayList<Hand>) hands.clone();
		
		//Removing the non highest hands (hands with combination lower than the highest combination found in the hands)
		ListIterator<Hand> iter = handsToCompare.listIterator();
		
		for (int i = 0; i < handsToCompare.size(); i++) {
		    if(highestHand.getCombination().compareTo(handsToCompare.get(i).getCombination()) == 1) {
		        highestHand = handsToCompare.get(i);
		    }
		}
		
		while(iter.hasNext()){
		    if(iter.next().getCombination().compareTo(highestHand.getCombination()) == -1) {
		        iter.remove();
		    }
		}

		//If only one item is remaining, this is the highest hand
		if(handsToCompare.size() == 1)
			return handsToCompare;
		
		return compareHands(handsToCompare);
	}
}
