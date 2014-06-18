package poker.game.model;

public enum BetType {
	CHECK,
	FOLD,
	CALL, //Bet as much as the (current) highest bet
	RAISE;
}
