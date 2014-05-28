package poker.card.helper;

import java.util.ArrayList;


public class CustomPickSequence {

	private ArrayList<Boolean> customPickSequence;
		
	public CustomPickSequence(){
		this.customPickSequence = new ArrayList<Boolean>();
	}
	
	public CustomPickSequence(ArrayList<Boolean> customPickSequence){
		this.customPickSequence = new ArrayList<Boolean>();
	}
	
	public CustomPickSequence pickNextCard(){
		this.customPickSequence.add(true);
		return this;
	}
	
	public CustomPickSequence pickNextCards(int nbCards){
		for(int i = 0; i<nbCards; i++){
			this.customPickSequence.add(true);
		}
		return this;
	}
	
	public CustomPickSequence burnNextCard(){
		this.customPickSequence.add(false);
		return this;
	}

	public CustomPickSequence burnNextCards(int nbCards){
		for(int i = 0; i<nbCards; i++){
			this.customPickSequence.add(false);
		}
		return this;
	}
	
	public CustomPickSequence alternateBurnPickNextCard(){
		this.customPickSequence.add(false);
		this.customPickSequence.add(true);
		
		return this;
	}
	
	public CustomPickSequence alternateBurnPickNextCards(int nbCards){
		for(int i = 0; i<nbCards; i++){
			this.customPickSequence.add(false);
			this.customPickSequence.add(true);
		}
		return this;
	}
	
	public boolean isCardPickedUp(int cardIndex){
		return cardIndex <= this.customPickSequence.size() && this.customPickSequence.get(cardIndex - 1) == true;
	}
	
	public int getNumberCardsLookedUp(){
		return this.customPickSequence.size();
	}
	
	public static CustomPickSequence getFixedNumberCardsPickedDealSequence(int nbCardsPicked){
		CustomPickSequence fixedNumberCardsPickedDealSequence = new CustomPickSequence();
		return fixedNumberCardsPickedDealSequence.pickNextCards(nbCardsPicked);
	}
	
	public static CustomPickSequence getHoldemFlopThroughRiverDealSequence(){
		CustomPickSequence holdemFlopToRiverDealSequence = new CustomPickSequence();
		holdemFlopToRiverDealSequence.burnNextCard().pickNextCards(3).alternateBurnPickNextCards(2);
		
		return holdemFlopToRiverDealSequence;
	}
	
	public static CustomPickSequence getHoldemTurnThroughRiverDealSequence(){
		CustomPickSequence holdemTurnThroughRiverDealSequence = new CustomPickSequence();
		holdemTurnThroughRiverDealSequence.alternateBurnPickNextCards(2);
		
		return holdemTurnThroughRiverDealSequence;
	}
	
	public static CustomPickSequence getHoldemRiverDealSequence(){
		CustomPickSequence holdemRiverDealSequence = new CustomPickSequence();
		
		return holdemRiverDealSequence.alternateBurnPickNextCard();
	}
	
	public static CustomPickSequence getHoldemFlopDealSequence(){
		CustomPickSequence holdemRiverDealSequence = new CustomPickSequence();
		
		return holdemRiverDealSequence.burnNextCard().pickNextCards(3);
	}
}
