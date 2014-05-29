package poker.card.heuristics.combination.model;

import java.util.ArrayList;
import java.util.Comparator;

import poker.card.heuristics.combination.helper.HandHelper;
import poker.card.model.Card;
import poker.card.model.CardRank;

public class Hand {
	
	//Example with these cards : 2, 2, 4, 6, 7
	
	/**
	 * Combination type of the hand. Ex: One pair
	 */
	Combination combination;
	
	/**
	 * Cards that compose the combination. Ex: 2 and 2 for a one pair.
	 */
	ArrayList<Card> combinationCards;
	
	/**
	 * Cards of highest rank that complete the hand (max 5 cards for a hand). Ex: 4 6 7
	 */
	ArrayList<Card> additionalCards;
	
	public Hand(Combination combination, ArrayList<Card> combinationCards) {
		this.combination = combination;
		this.combinationCards = combinationCards;
		this.additionalCards = new ArrayList<Card>();
	}
	
	public Hand(Combination combination, ArrayList<Card> combinationCards, ArrayList<Card> additionalCards) {
		this.combination = combination;
		this.combinationCards = combinationCards;
		this.additionalCards = additionalCards;
	}
	
	public Hand() {

	}

	public Combination getCombination() {
		return combination;
	}

	public void setCombination(Combination combination) {
		this.combination = combination;
	}

	public ArrayList<Card> getCombinationCards() {
		return combinationCards;
	}

	public void setCombinationCards(ArrayList<Card> cards) {
		this.combinationCards = cards;
	}
	
	public String toString() {
		String hand =
				"---------------\nHand: \n"
				+ "Combination: " + getCombination() + "\n"
				+ "Cards: "
				+ combinationCards.toString()
				+ "\n";
				
		if(additionalCards.size() > 0) {
			hand += "Additional cards (highest cards): "
				 + additionalCards.toString()
			     + "\n";
		}
		
		hand += "---------------";
		
		return hand;
	}

	public ArrayList<Card> getAdditionalCards() {
		return additionalCards;
	}

	public void setAdditionalCards(ArrayList<Card> additionalCards) {
		this.additionalCards = additionalCards;
	}
	
	public ArrayList<Card> getAllCards() {
		ArrayList<Card> allCards = (ArrayList<Card>) combinationCards.clone();
		
		allCards.addAll(additionalCards);
		
		return allCards;
	}
}
