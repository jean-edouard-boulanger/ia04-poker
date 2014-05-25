package poker.card.heuristics.combination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import poker.card.heuristics.combination.exception.EmptyCardListException;
import poker.card.heuristics.combination.exception.UnexpectedCombinationIdenticCards;
import poker.card.heuristics.combination.model.Combination;
import poker.card.heuristics.combination.model.Hand;
import poker.card.model.Card;
import poker.card.model.CardRank;
import poker.card.model.CardSuit;
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
	
	/**
	 * 
	 * @param cards
	 * @param minimumIdentic If 0, returns all list containing more than one card of the same rank. Else returns the lists containing the exact number of cards of the same rank.
	 * @return
	 * @throws EmptyCardListException
	 */
	private static Map<CardRank, ArrayList<Card>> allPairs(ArrayList<Card> cards, int minimumIdentic) throws EmptyCardListException {
		if(cards == null || cards.size() == 0) {
			throw new EmptyCardListException();
		}
		
		if(cards.size() == 1) {
			//One card: can't have a pair
			return null;
		}
		
		Map<CardRank, ArrayList<Card>> pairsMap = new HashMap<CardRank, ArrayList<Card>>();
		
		int count = cards.size();
		
		for (int i = 0; i < count; i++) {
			Card card = cards.get(i);
			CardRank rank = card.getRank();
			
			if(!pairsMap.containsKey(rank)) {
				//Card rank does not exist in the map yet: create a new list for it
				ArrayList<Card> list = new ArrayList<Card>();
				pairsMap.put(rank, list);
			}
			
			pairsMap.get(rank).add(card);
		}
		
		Map<CardRank, ArrayList<Card>> finalPairsMap = new HashMap<CardRank, ArrayList<Card>>();
		
		//Keeping only the long enough lists
		for(Entry<CardRank, ArrayList<Card>> entry : pairsMap.entrySet()) {
			if(entry.getValue().size() >= 2 && !(minimumIdentic > 0 && entry.getValue().size() < minimumIdentic)) {
				finalPairsMap.put(entry.getKey(), entry.getValue());				
			}
		}
		
		return finalPairsMap;
	}
	
	/**
	 * 
	 * @param pairsMap Map containing lists of cards.
	 * @param totalIdenticCards Size of the list we need (2, 3 or 4 cards of the same rank)
	 * @return List of totalIdenticCards cards with the highest (and same) rank.
	 */
	public static ArrayList<Card> listOfHighestRankFromMap(Map<CardRank, ArrayList<Card>> pairsMap, int totalIdenticCards) {
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
		
		while(pairCards.size() > totalIdenticCards) {
			pairCards.remove(0);
		}

		return pairCards;
	}
	
	/**
	 * 
	 * @param cards
	 * @param totalIdenticCards 2 to get highest one pair. 3 to get highest three of a king. 4 to get highest four of a king.
	 * @return Highest one pair, three of a king or four of a king hand of a card list
	 * @throws UnexpectedCombinationIdenticCards
	 * @throws EmptyCardListException
	 */
	public static Hand highestOfKing(ArrayList<Card> cards, int totalIdenticCards) throws UnexpectedCombinationIdenticCards, EmptyCardListException {
		if(totalIdenticCards < 2 || totalIdenticCards > 4)
			throw new UnexpectedCombinationIdenticCards(totalIdenticCards);
		
		if(cards == null || cards.size() == 0) {
			throw new EmptyCardListException();
		}
		
		Map<CardRank, ArrayList<Card>> pairsMap = allPairs(cards, totalIdenticCards);
		
		ArrayList<Card> pairCards = listOfHighestRankFromMap(pairsMap, totalIdenticCards);
		
		if(pairCards == null)
			return null;
		
		Combination combination = null;
		
		switch (totalIdenticCards) {
		case 2:
			combination = Combination.ONE_PAIR;
			break;
		case 3:
			combination = Combination.THREE_OF_A_KIND;
			break;
		case 4:
			combination = Combination.FOUR_OF_A_KIND;
			break;
		}
		
		return new Hand(combination, pairCards);
	}
		
	/**
	 * 
	 * @param cards
	 * @return Highest two pair in the list of cards
	 * @throws EmptyCardListException
	 */
	public static Hand highestTwoPair(ArrayList<Card> cards) throws EmptyCardListException {
		if(cards == null || cards.size() == 0) {
			throw new EmptyCardListException();
		}
		
		Map<CardRank, ArrayList<Card>> pairsMap = allPairs(cards, 2);
		
		if(pairsMap == null || pairsMap.size() == 1)
			return null;
		
		CardRank highestRank1 = null;
		CardRank highestRank2 = null;
		
		//Iterating on all the entries of the map to find the highest rank, with highestRank1 > highestRank2
		for(Entry<CardRank, ArrayList<Card>> entry : pairsMap.entrySet()) {
			ArrayList<Card> list = entry.getValue();
				
			if(highestRank1 == null) {
				//No pair already found
				highestRank1 = list.get(0).getRank();
			}
			else if(highestRank2 == null) {
				//Only one pair found
				if(highestRank1.getCardRank() < list.get(0).getRank().getCardRank()) {
					highestRank2 = highestRank1;
					highestRank1 = list.get(0).getRank();
				}
				else
					highestRank2 = list.get(0).getRank();				
			}
			else if(highestRank1.getCardRank() < list.get(0).getRank().getCardRank()) {
				//Better than both best pairs found
				highestRank2 = highestRank1;
				highestRank1 = list.get(0).getRank();
			}
			else if(highestRank2.getCardRank() < list.get(0).getRank().getCardRank()) {
				//Pair better than only one of the 2 current best
				highestRank2 = list.get(0).getRank();
			}
		}
		
		//Making the two pair list with the two highest rank lists
		while(pairsMap.get(highestRank1).size() > 2) {
			pairsMap.get(highestRank1).remove(0);
		}
		
		while(pairsMap.get(highestRank2).size() > 2) {
			pairsMap.get(highestRank2).remove(0);
		}
		
		ArrayList<Card> pairCards = pairsMap.get(highestRank1);
		
		pairCards.addAll(pairsMap.get(highestRank2));
		
		return new Hand(Combination.TWO_PAIR, pairCards);
	}
	
	public static Hand highestFullHouse(ArrayList<Card> cards) throws UnexpectedCombinationIdenticCards, EmptyCardListException {
		if(cards == null || cards.size() == 0) {
			throw new EmptyCardListException();
		}
		
		Hand combinationCards = highestOfKing(cards, 3);
		
		//No three of a king
		if(combinationCards == null)
			return null;
		
		//Copying cards not to remove cards from the tested list.
		ArrayList<Card> cardsToTest = (ArrayList<Card>) cards.clone();
		
		//Removing the three of a king found not to find it as the best one pair.
		cardsToTest.removeAll(combinationCards.getCards());
		
		Hand highestOnePair = highestOfKing(cardsToTest, 2);
		
		//No pair
		if(highestOnePair == null)
			return null;
		
		//Pair and three of a king: merge them to make the full house
		combinationCards.getCards().addAll(highestOnePair.getCards());
		
		return new Hand(Combination.FULL_HOUSE, combinationCards.getCards());
	}
	
	public static Hand highestFlush(ArrayList<Card> cards) throws EmptyCardListException {
		if(cards == null || cards.size() == 0) {
			throw new EmptyCardListException();
		}

		//Need 5 cards at least to get a flush
		if(cards.size() < 5)
			return null;
		
		Map<CardSuit, ArrayList<Card>> suitMap = new HashMap<CardSuit, ArrayList<Card>>();
		
		for(Card card : cards) {
			CardSuit suit = card.getSuit();
			
			if(!suitMap.containsKey(suit)) {
				//Card rank does not exist in the map yet: create a new list for it
				ArrayList<Card> list = new ArrayList<Card>();
				suitMap.put(suit, list);
			}
			
			suitMap.get(suit).add(card);
		}
		
		//Iterating over the map to see if a suit contains at least 5 cards
		for(Entry<CardSuit, ArrayList<Card>> entry : suitMap.entrySet()) {
			
			ArrayList<Card> cardList = entry.getValue();
			
			if(cardList.size() >= 5) {
				//Removing the lowest cards if more than 6
				while(cardList.size() > 5) {
					Card lowestRankCard = cardList.get(0);
					
					for (int i = 1; i < cardList.size(); i++) {
						if(lowestRankCard.compareTo(cardList.get(i)) == 1)
							lowestRankCard = cardList.get(i);
					}
					
					cardList.remove(lowestRankCard);
				}
				
				return new Hand(Combination.FLUSH, cardList);
			}
		}
		
		//No flush found
		return null;
	}
	
	public static Hand playerHandWithGame(UserDeck userDeck) throws EmptyCardListException {
		ArrayList<Card> communityCards = CommunityCards.getInstance().getCommunityCards();
		
		highestCard(communityCards);
		
		return new Hand();
	}
}
