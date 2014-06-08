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
	private int currentPlayerIndex = 0;
	
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

	public Player getPlayerByAID(AID aid){
		for(Player p : this.gamePlayers){
			if(p.getAID().equals(aid)){
				return p;
			}
		}
		return null;
	}
	
	public Player getCurrentPlayer(){
		return this.gamePlayers.get(this.currentPlayerIndex);
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayerIndex = gamePlayers.indexOf(currentPlayer);
	}


	/**
	 * Get a player by it's name
	 * @param playerName	Player name.
	 * @return Player with the given name or null if no player were found.
	 */
	public Player getPlayerByName(String playerName) {
		for(Player p : this.gamePlayers){
			if(p.getPlayerName() == playerName){
				return p;
			}
		}
		return null;
	}
	
}
