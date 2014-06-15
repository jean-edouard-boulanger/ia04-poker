package poker.game.model;

public enum Round {
    PLAYER_CARDS_DEAL(0),
    PREFLOP(1), 
    FLOP(2), 
    TURN(3), 
    RIVER(4),
    SHOWDOWN(5);

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
