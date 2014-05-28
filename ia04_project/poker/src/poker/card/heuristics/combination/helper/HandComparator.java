package poker.card.heuristics.combination.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import poker.card.heuristics.combination.model.Hand;

public class HandComparator {
	
	protected static Hand compareHands(ArrayList<Hand> hands) {
		switch(hands.get(0).getCombination()) {
		case ONE_PAIR:
			return null;
		case TWO_PAIR:
			return null;
		case THREE_OF_A_KIND:
			return null;
		case FLUSH:
			return null;
		case FOUR_OF_A_KIND:
			return null;
			
		default:
			return null;
		}
	}
	
	public static Hand bestHand(ArrayList<Hand> hands) {
		if(hands == null || hands.size() == 0)
			return null;
		
		if(hands.size() == 1)
			return hands.get(0);
		
		Hand highestHand = hands.get(0);
		
		//Removing the non highest hands (hands with combination lower than the highest combination found in the hands)
		ListIterator<Hand> iter = hands.listIterator();
		
		for (int i = 0; i < hands.size(); i++) {
		    if(highestHand.getCombination().compareTo(hands.get(i).getCombination()) == 1) {
		        highestHand = hands.get(i);
		    }
		}
		
		while(iter.hasNext()){
		    if(iter.next().getCombination().compareTo(highestHand.getCombination()) == -1) {
		        iter.remove();
		    }
		}

		//If only one item is remaining, this is the highest hand
		if(hands.size() == 1)
			return hands.get(0);
		
		return compareHands(hands);
	}
}
