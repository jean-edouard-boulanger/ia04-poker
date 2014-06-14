package sma.agent.simulationAgent;

import poker.card.model.CardRank;

public enum Round {
    
    preflop(0), 
    flop(1), 
    turn(2), 
    river(3),
    showdown(4);
    
    private int cardRank;
    
    private Round(int rank) {
	this.cardRank = rank;
    }

    public int getRound() {
	return cardRank;
    }

    public Round getNext() {
	if(cardRank == values().length)
	    return null;
	return values()[(ordinal()+1)];
    }
}
