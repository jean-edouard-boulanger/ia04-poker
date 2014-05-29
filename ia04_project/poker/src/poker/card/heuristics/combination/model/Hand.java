package poker.card.heuristics.combination.model;

import java.util.ArrayList;
import java.util.Comparator;

import poker.card.heuristics.combination.helper.HandHelper;
import poker.card.model.Card;
import poker.card.model.CardRank;

public class Hand {
	Combination combination;
	ArrayList<Card> combinationCards;
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
