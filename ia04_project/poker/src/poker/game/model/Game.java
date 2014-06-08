package poker.game.model;

import jade.core.AID;

import java.util.ArrayList;

import poker.card.model.GameDeck;
import poker.game.exception.NotRegisteredPlayerException;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.player.model.Player;
import poker.token.model.TokenSet;
import poker.token.model.TokenValueDefinition;

public class Game {
	
	private ArrayList<Player> gamePlayers;
	private GameDeck gameDeck;
	private TokenSet pot;
	private TokenValueDefinition tokenValueDefinition = null;
	private BlindValueDefinition blindValueDefinition = null;
	private Player currentPlayer = null;
	
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

	public void setCurrentPlayer(Player p) throws NotRegisteredPlayerException{
		if(!this.gamePlayers.contains(p)){
			throw new NotRegisteredPlayerException(p);
		}
		this.currentPlayer = p;
	}
	
	public Player getCurrentPlayer(){
		return this.currentPlayer;
	}
	
	public Player getPlayerByAID(AID playerAID){
		for(Player player : this.gamePlayers){
			if(player.getAID().equals(playerAID)){
				return player;
			}
		}
		return null;
	}
	
	public void addPlayer(Player p) throws PlayerAlreadyRegisteredException{
		if(this.getPlayerByAID(p.getAID()) != null){
			throw new PlayerAlreadyRegisteredException(p);
		}
		this.gamePlayers.add(p);
	}
	
	/**
	 * Get a player by it's name
	 * @param playerName	Player name.
	 * @return Player with the given name or null if no player were found.
	 */
	public Player getPlayerByName(String playerName) {
		for(Player p : this.gamePlayers){
			if(p.getPlayerName().equals(playerName)){
				return p;
			}
		}
		return null;
	}
	
}
