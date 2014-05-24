package poker.game.player.model;

import poker.card.model.UserDeck;
import poker.token.helpers.TokenSetValueEvaluator;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import poker.token.model.TokenValueDefinition;

public abstract class Player {
	
	protected String id;
	protected String playerName;
	protected int tablePositionIndex;
	protected PlayerStatus status;
	protected PlayerRole role;
	
	protected UserDeck deck;
	protected TokenSet tokens;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public int getTablePositionIndex() {
		return tablePositionIndex;
	}
	
	public void setTablePositionIndex(int tablePositionIndex) {
		this.tablePositionIndex = tablePositionIndex;
	}
	
	public UserDeck getDeck() {
		return deck;
	}
	
	public void setDeck(UserDeck deck) {
		this.deck = deck;
	}
	
	public TokenSet getTokens() {
		return tokens;
	}
	
	public void setTokens(TokenSet tokens) {
		this.tokens = tokens;
	}
	
	public PlayerStatus getStatus() {
		return status;
	}

	public void setStatus(PlayerStatus status) {
		this.status = status;
	}
	
	public PlayerRole getRole(){
		return role;
	}
	
	public void setRole(PlayerRole role){
		this.role = role;
	}
	
	public int getBankroll(TokenValueDefinition tokenValueDefinition){
		return TokenSetValueEvaluator.evaluateTokenSetValue(tokenValueDefinition, this.tokens);
	}
}
