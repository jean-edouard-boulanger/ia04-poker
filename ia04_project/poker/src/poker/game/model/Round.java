package poker.game.model;

public enum Round {
	PLAYER_CARDS_DEAL(0),
	PREFLOP(1), 
	FLOP(2), 
	TURN(3), 
	RIVER(4),
	SHOWDOWN(5);

	private int modelRank;

	private Round(int rank) {
		this.modelRank = rank;
	}

	public int getRound() {
		return modelRank;
	}

	public Round getNext() {
		if(modelRank == values().length)
			return null;
		return values()[(ordinal()+1)];
	}	
}
