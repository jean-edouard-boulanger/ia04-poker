package poker.card.handler.combination;

import java.util.ArrayList;

import poker.card.handler.combination.exception.EmptyCardListException;
import poker.card.handler.combination.model.Combination;
import poker.card.handler.combination.model.Hand;
import poker.card.model.Card;
import poker.card.model.CommunityCards;
import poker.card.model.UserDeck;

public class CardCombinations {
	
	/**
	 * 
	 * @param cards
	 * @return Highest card of a card list
	 * @throws EmptyCardListException
	 */
	public static Hand highestCard(ArrayList<Card> cards) throws EmptyCardListException {
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
		
		ArrayList<Card> handCard = new ArrayList<Card>();
		
		handCard.add(card1);
		
		return new Hand(Combination.HIGHT_CARD, handCard);
	}
	
	public static Hand playerHandWithGame(UserDeck userDeck) throws EmptyCardListException {
		ArrayList<Card> communityCards = CommunityCards.getInstance().getCommunityCards();
		
		highestCard(communityCards);
		
		return new Hand();
	}
}
