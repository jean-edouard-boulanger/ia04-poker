package gui.player.event.model;

import poker.token.model.TokenSet;

public class PlayerReceivedTokenSetEventData {
	
	private TokenSet tokenSet;
	private int tokenSetValuation;
	private int playerIndex;
	
	public PlayerReceivedTokenSetEventData(TokenSet tokenSet, int tokenSetValuation, int playerIndex){
		this.tokenSet = tokenSet;
		this.tokenSetValuation = tokenSetValuation;
		this.playerIndex = playerIndex;
	}
	
	public PlayerReceivedTokenSetEventData(){}

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