package poker.game.player.model;

import jade.core.AID;

import java.util.Comparator;

import javax.swing.text.StyledEditorKit.BoldAction;

import poker.card.model.UserDeck;
import poker.token.helpers.TokenSetValueEvaluator;
import poker.token.model.TokenSet;
import poker.token.model.TokenValueDefinition;

public class Player {
	
	protected AID aid;
	protected String nickname;
	protected Integer tablePositionIndex;
	protected PlayerStatus status;
	boolean isDealer;
	
	protected UserDeck deck;
	protected TokenSet tokens;
	
	public Player(){
		this(null, "");
	}
	
	public Player(AID aid, String playerName) {
		this.aid = aid;
		this.nickname = playerName;
		this.tokens = new TokenSet();
		this.status = PlayerStatus.IN_GAME;
		this.isDealer = false;
		this.deck = new UserDeck();
	}

	public AID getAID() {
		return aid;
	}

	public void setAID(AID aid) {
		this.aid = aid;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String playerName) {
		this.nickname = playerName;
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
	
	public boolean isDealer(){
		return this.isDealer;
	}
	
	public void setDealer(boolean isDealer){
		this.isDealer = isDealer;
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
