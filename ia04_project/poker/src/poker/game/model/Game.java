package poker.game.model;

import jade.core.AID;

import java.util.ArrayList;

import poker.card.model.GameDeck;
import poker.game.player.model.Player;
import poker.token.model.TokenSet;
import poker.token.model.TokenValueDefinition;

public class Game {
	
	private ArrayList<Player> gamePlayers;
	private GameDeck gameDeck;
	private TokenSet pot;
	private TokenValueDefinition tokenValueDefinition = null;
	private BlindValueDefinition blindValueDefinition = null;
	private Player currentPlayer;
	
	private boolean server_started = false;
	
	public Game(){}
	
	public void setGamePlayers(ArrayList<Player> gamePlayers){
		this.gamePlayers = gamePlayers;
	}
	
	public ArrayList<Player> getGamePlayers(){
		return this.gamePlayers;
	}

	public GameDeck getGameDeck() {
		return gameDeck;
	}

	public void setGameDeck(GameDeck gameDeck) {
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

	public Player getPlayerById(AID id){
		for(Player p : this.gamePlayers){
			if(p.getId() == id){
				return p;
			}
		}
		return null;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public boolean isServer_started() {
		return server_started;
	}

	public void setServer_started(boolean server_started) {
		this.server_started = server_started;
	}
}
