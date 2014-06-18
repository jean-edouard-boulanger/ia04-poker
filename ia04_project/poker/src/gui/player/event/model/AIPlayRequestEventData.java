package gui.player.event.model;

import java.util.ArrayList;

import poker.card.model.Card;

public class AIPlayRequestEventData extends PlayRequestEventData {

	public AIPlayRequestEventData() {
		super();
	}
	
	private ArrayList<Card> cards;

	public ArrayList<Card> getCards() {
		return cards;
	}

	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}	
}
