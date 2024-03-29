package gui.player.event.model;

import poker.token.model.TokenSet;

public class PlayerTokenSetChangedEventData {
	
	private TokenSet tokenSet;
	private int tokenSetValuation;
	private int playerIndex;
	
	public PlayerTokenSetChangedEventData(TokenSet tokenSet, int tokenSetValuation, int playerIndex){
		this.tokenSet = tokenSet;
		this.tokenSetValuation = tokenSetValuation;
		this.playerIndex = playerIndex;
	}
	
	public PlayerTokenSetChangedEventData(){}

	public TokenSet getTokenSet() {
		return tokenSet;
	}

	public void setTokenSet(TokenSet tokenSet) {
		this.tokenSet = tokenSet;
	}

	public int getTokenSetValuation() {
		return tokenSetValuation;
	}

	public void setTokenSetValuation(int tokenSetValuation) {
		this.tokenSetValuation = tokenSetValuation;
	}

	public int getPlayerIndex() {
		return playerIndex;
	}

	public void setPlayerIndex(int playerIndex) {
		this.playerIndex = playerIndex;
	}
}