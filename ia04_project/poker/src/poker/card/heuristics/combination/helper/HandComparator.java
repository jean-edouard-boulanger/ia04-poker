package poker.card.heuristics.combination.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import poker.card.heuristics.combination.exception.EmptyCardListException;
import poker.card.heuristics.combination.model.Combination;
import poker.card.heuristics.combination.model.Hand;
import poker.card.model.Card;
import poker.card.model.CardRank;

public class HandComparator {
	
	/**
	 * Compare hands that have the same Combination, and these combination are of the same rank.
	 * Exemple: 2 One pair hands of (2, 2) => The 3 other highest cards of each hand will be used to determine the best hand.
	 * @param hands
	 * @return Highest hands (more than one if equality)
	 */
	private static ArrayList<Hand> handsWithHighestList(ArrayList<Hand> hands) {
		if(hands.size() == 0)
			return hands;
		
		//Reverse binary tree containing lists of hands for each value. 
		Map<CardRank, ArrayList<Hand>> handsMap = new TreeMap<CardRank, ArrayList<Hand>>(Collections.reverseOrder());
		
		for(Hand h : hands) {
			for(Card c : h.getAdditionalCards()) {
				if(!handsMap.containsKey(c.getRank())) {
					handsMap.put(c.getRank(), new ArrayList<Hand>());
				}
				handsMap.get(c.getRank()).add(h);
			}
		}
		
		for(Map.Entry<CardRank,ArrayList<Hand>> entry : handsMap.entrySet()) {
			ArrayList<Hand> entryHands = entry.getValue();
			
			//Removing elements in the lists while iterating: sometimes a list can have become empty.
			if(entryHands.size() == 0)
				continue;
			
			if(entryHands.size() == 1) {
				//Only one hand has the linked value which is currently the highest value of the tree:
				hands.clear();
				
				hands.add(entryHands.get(0));
				
				return hands;
			}
			
			//Removing all hands which don't have card in the current highest value
			ListIterator<Hand> iter = hands.listIterator();
			
			while(iter.hasNext()){
				Hand currentHand = iter.next();
			    if(!entryHands.contains(currentHand)) {
			        iter.remove();
					//Removing these hands from the tree
					for(Map.Entry<CardRank,ArrayList<Hand>> entry2 : handsMap.entrySet()) {
						while (entry2.getValue().contains(currentHand)) {
							entry2.getValue().remove(currentHand);
						}
					}
			    }
			}			
		}
		
		return hands;
	}
	
	/**
	 * Algorithm works for 1, 2, 3, 4 identical 4 cards: take the first card of the combination cards list to compare them
	 * @param hands
	 * @return
	 * @throws EmptyCardListException 
	 */
	private static ArrayList<Hand> bestIdenticalCardsHands(ArrayList<Hand> hands) throws EmptyCardListException {		
		CardRank highestRank = HandHelper.getCombinationRank(hands.get(0));
		
		for(Hand h : hands) {
			if(highestRank.compareTo(HandHelper.getCombinationRank(h)) < 0) {
				highestRank = HandHelper.getCombinationRank(h);
			}
		}
		
		ListIterator<Hand> iter = hands.listIterator();
		
		while(iter.hasNext()){
		    if(HandHelper.getCombinationRank(iter.next()).compareTo(highestRank) < 0) {
		        iter.remove();
		    }
		}

		//Only hands with the same rank (highest rank in the list) are in the list
		if(hands.size() == 1)
			return hands;
		
		return handsWithHighestList(hands);
	}
	
	private static ArrayList<Hand> compareHands(ArrayList<Hand> hands) {
		switch(hands.get(0).getCombination()) {
		case HIGH_CARD:
		case ONE_PAIR:
		case THREE_OF_A_KIND:
		case FOUR_OF_A_KIND:
			try {
				return bestIdenticalCardsHands(hands);
			} catch (EmptyCardListException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		case TWO_PAIR:
			return null;
		case STRAIGHT:
			return null;
		case FLUSH:
			return null;
		case FULL_HOUSE:
			return null;
		case STRAIGHT_FLUSH:
			return null;
			
		default:
			return null;
		}
	}
	
	/**
	 * Determine the best (or the list of best if equality) hands. Clone the hands not to change the given data.
	 * @param hands
	 * @return
	 */
	public static ArrayList<Hand> bestHand(ArrayList<Hand> hands) {
		if(hands == null || hands.size() == 0)
			return null;
		
		if(hands.size() == 1)
			return hands;
		
		Hand highestHand = hands.get(0);
		
		//Cloning the given data not to change it
		ArrayList<Hand> handsToCompare = (ArrayList<Hand>) hands.clone();
		
		//Removing the non highest hands (hands with combination lower than the highest combination found in the hands)
		ListIterator<Hand> iter = handsToCompare.listIterator();
		
		for (int i = 0; i < handsToCompare.size(); i++) {
		    if(highestHand.getCombination().compareTo(handsToCompare.get(i).getCombination()) < 0) {
		        highestHand = handsToCompare.get(i);
		    }
		}
		
		while(iter.hasNext()){
		    if(iter.next().getCombination().compareTo(highestHand.getCombination()) < 0) {
		        iter.remove();
		    }
		}

		//If only one item is remaining, this is the highest hand
		if(handsToCompare.size() == 1)
			return handsToCompare;
		
		return compareHands(handsToCompare);
	}
}
