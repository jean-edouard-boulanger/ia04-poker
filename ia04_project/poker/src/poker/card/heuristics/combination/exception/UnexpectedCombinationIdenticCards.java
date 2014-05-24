package poker.card.heuristics.combination.exception;

public class UnexpectedCombinationIdenticCards extends Exception {
	public UnexpectedCombinationIdenticCards(int multiple){
		super("Can't find a combination of " + multiple + " cards with the same rank.");
	}
}
