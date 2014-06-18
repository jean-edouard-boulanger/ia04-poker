package poker.game.model;

public class Decision {
	
	private BetType betType;
	private int betAmount;
	
	public Decision(){}
	
	public Decision(BetType betType, int betAmount) {
		this.betType = betType;
		this.betAmount = betAmount;
	}

	public Decision(BetType betType) {
		this.betType = betType;
		this.betAmount = 0;
	}

	public BetType getBetType() {
		return betType;
	}

	public void setBetType(BetType betType) {
		this.betType = betType;
	}

	public int getBetAmount() {
		return betAmount;
	}

	public void setBetAmount(int betAmount) {
		this.betAmount = betAmount;
	}
	
	public String toString() {
		return new String("Decided to " + betType + " with amount: " + betAmount);
	}
}
