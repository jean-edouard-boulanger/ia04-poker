package poker.game.player.model;

import java.util.Comparator;

import javax.print.attribute.standard.MediaSize.Other;

import jade.core.AID;
import poker.card.model.UserDeck;
import poker.token.helpers.TokenSetValueEvaluator;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import poker.token.model.TokenValueDefinition;

public class Player {
	
	protected AID aid;
	protected String playerName;
	protected Integer tablePositionIndex;
	protected PlayerStatus status;
	protected PlayerRole role;
	
	protected UserDeck deck;
	protected TokenSet tokens;
	
	public Player(){}
	
	public Player(AID aid, String playerName) {
		this.aid = aid;
		this.playerName = playerName;
	}

	public AID getAID() {
		return aid;
	}

	public void setAID(AID aid) {
		this.aid = aid;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public Integer getTablePositionIndex() {
		return tablePositionIndex;
	}
	
	public void setTablePositionIndex(Integer tablePositionIndex) {
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
	
	public PlayerStatus getStatus(){
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
	
	@Override
	public boolean equals(Object o){
		if(o == null) return false;
		if(o == this) return true;
		if(!(o instanceof Player)) return false;
		
		return ((Player)o).getAID().equals(this.getAID());
	}
	
	public static class PlayerTablePositionComparator implements Comparator<Player>{
		@Override
		public int compare(Player p1, Player p2){
			if(p1.getTablePositionIndex() < p2.getTablePositionIndex()){
				return -1;
			}
			else if(p1.getTablePositionIndex() > p2.getTablePositionIndex()){
				return 1;
			}
			else{
				return 0;
			}
		}
	}
}
