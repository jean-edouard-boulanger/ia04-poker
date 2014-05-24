package poker.card.model;

import java.util.ArrayList;

import poker.card.exception.CommunityCardsFullException;

public class CommunityCards{
	
	private static final int NBCARDS = 5;
	
	private static CommunityCards instance = null;
	private ArrayList<Card> communityCards;
		
	private CommunityCards(){
		this.communityCards = new ArrayList<Card>();
	}
	
	public static CommunityCards getInstance(){
		if(instance == null){
			instance = new CommunityCards();
		}
		return instance;
	}
	
	public void pushCard(Card card) throws CommunityCardsFullException{
		if(this.communityCards.size() >= NBCARDS){
			throw new CommunityCardsFullException();
		}
		this.communityCards.add(card);
	}
	
	public ArrayList<Card> popCards(){
		ArrayList<Card> communityCardsTmp = new ArrayList<Card>(this.communityCards);
		this.communityCards.clear();
		return communityCardsTmp;
	}

	public ArrayList<Card> getCommunityCards() {
		return communityCards;
	}

	public void setCommunityCards(ArrayList<Card> communityCards) {
		this.communityCards = communityCards;
	}
}
