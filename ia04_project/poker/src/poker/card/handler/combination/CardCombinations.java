package poker.card.handler.combination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import poker.card.handler.combination.exception.EmptyCardListException;
import poker.card.handler.combination.model.Combination;
import poker.card.handler.combination.model.Hand;
import poker.card.model.Card;
import poker.card.model.CardRank;
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
		
		return new Hand(Combination.HIGH_CARD, handCard);
	}
	
	private static Map<CardRank, ArrayList<Card>> allPairs(ArrayList<Card> cards) throws EmptyCardListException {
		if(cards == null || cards.size() == 0) {
			throw new EmptyCardListException();
		}
		
		if(cards.size() == 1) {
			//One card: can't have a pair
			return null;
		}
		
		Map<CardRank, ArrayList<Card>> pairsMap = new HashMap<CardRank, ArrayList<Card>>();
		
		int count = cards.size();
		
		for (int i = 0; i < count - 1; i++) {
			Card card = cards.get(i);
			CardRank rank = card.getRank();
			
			if(!pairsMap.containsKey(rank)) {
				//Card rank does not exist in the map yet: create a new list for it
				ArrayList<Card> list = new ArrayList<Card>();
				pairsMap.put(rank, list);
			}
			
			pairsMap.get(rank).add(card);
		}
		
		for(Entry<CardRank, ArrayList<Card>> entry : pairsMap.entrySet()) {
			if(entry.getValue().size() < 2)
				pairsMap.remove(entry.getKey());
		}
		
		return pairsMap;
	}
	
	/**
	 * 
	 * @param cards
	 * @return Highest one pair hand of a card list
	 * @throws EmptyCardListException
	 */
	public static Hand highestOnePair(ArrayList<Card> cards) throws EmptyCardListException {
		if(cards == null || cards.size() == 0) {
			throw new EmptyCardListException();
		}
		
//		Collections.sort(cards, new Card.CardComparator());
		Map<CardRank, ArrayList<Card>> pairsMap = allPairs(cards);
		
		if(pairsMap == null || pairsMap.size() == 0)
			return null;
		
		CardRank highestRank = null;
		
		//Iterating on all the entries of the map to find the highest rank
		for(Entry<CardRank, ArrayList<Card>> entry : pairsMap.entrySet()) {
			ArrayList<Card> list = entry.getValue();
				
			if(highestRank == null) {
				//No pair already found
				highestRank = list.get(0).getRank();
			}
			else if(highestRank.getCardRank() < list.get(0).getRank().getCardRank()) {
				//Better pair found
				highestRank = list.get(0).getRank();
			}
		}
		
		ArrayList<Card> pairCards = pairsMap.get(highestRank);
		
		if(pairCards.size() > 2) {
			while(pairCards.size() > 2) {
				pairCards.remove(0);
			}
		}
		
		return new Hand(Combination.ONE_PAIR, pairCards);
	}
	
	public static Hand playerHandWithGame(UserDeck userDeck) throws EmptyCardListException {
		ArrayList<Card> communityCards = CommunityCards.getInstance().getCommunityCards();
		
		highestCard(communityCards);
		
		return new Hand();
	}
}
