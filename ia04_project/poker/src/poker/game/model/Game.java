package poker.game.model;

import poker.card.model.CardDeck;
import poker.card.model.CommunityCards;
import poker.token.model.TokenSet;
import poker.token.model.TokenValueDefinition;

public class Game {
	
	private PlayersContainer playersContainer;
	private CardDeck gameDeck;
	private TokenSet pot;
	private TokenValueDefinition tokenValueDefinition;
	private BlindValueDefinition blindValueDefinition;
	private CommunityCards communityCards;
	
	public Game(){
		this.playersContainer = new PlayersContainer();
		this.gameDeck = new CardDeck();
		this.pot = new TokenSet();
		this.tokenValueDefinition = new TokenValueDefinition();
		this.blindValueDefinition = new BlindValueDefinition();
		this.communityCards = new CommunityCards();
	}
	
	public void setPlayersContainer(PlayersContainer playersContainer){
		this.playersContainer = playersContainer;
	}
	
	public PlayersContainer getPlayersContainer(){
		return this.playersContainer;
	}

	public CardDeck getGameDeck() {
		return gameDeck;
	}

	public void setGameDeck(CardDeck gameDeck) {
		this.gameDeck = gameDeck;
	}

	public TokenValueDefinition getTokenValueDefinition() {
		return tokenValueDefinition;
	}

	public void setTokenValueDefinition(TokenValueDefinition tokenValueDefinition) {
		this.tokenValueDefinition = tokenValueDefinition;
	}

	public BlindValueDefinition getBlindValueDefinition() {
		return blindValueDefinition;
	}

	public void setBlindValueDefinition(BlindValueDefinition blindValueDefinition) {
		this.blindValueDefinition = blindValueDefinition;
	}

	public TokenSet getPot() {
		return pot;
	}

	public void setPot(TokenSet pot) {
		this.pot = pot;
	}

	public CommunityCards getCommunityCards() {
		return communityCards;
	}

	public void setCommunityCards(CommunityCards communityCards) {
		this.communityCards = communityCards;
	}
}
