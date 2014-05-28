package poker.card.heuristics.combination.helper;

import java.util.ArrayList;
import java.util.Collections;

import poker.card.heuristics.combination.model.Hand;
import poker.card.model.Card;

public class HandHelper {

	public static Card getHighestCardInHand(Hand h){
		ArrayList<Card> cards = new ArrayList<Card>(h.getCards());
		Collections.sort(cards, new Card.CardComparator());
		return cards.get(0);
	}
	
}
