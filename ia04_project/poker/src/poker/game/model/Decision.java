package poker.game.model;

public class Decision {
	
	private BetType betType;
	private int betAmount;
	
	public Decision(){}
	
	public Decision(BetType betType) {
		this.betType = betType;
		this.betAmount = betAmount;
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
}
