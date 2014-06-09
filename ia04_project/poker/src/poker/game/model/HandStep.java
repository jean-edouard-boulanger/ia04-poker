package poker.game.model;

public enum HandStep {
	PLAYER_CARDS_DEAL(1),
	PRE_FLOP(2),
	FLOP(3),
	TURN(4),
	RIVER(5);
	
	private int handStep;
	
	private HandStep(int handStep){
		this.handStep = handStep;
	}	
}
