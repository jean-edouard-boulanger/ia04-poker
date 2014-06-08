package poker.game.exception;

import poker.game.player.model.Player;

public class PlayerAlreadyRegisteredException extends Exception {

	public PlayerAlreadyRegisteredException(){
		super("Tried to add an already registrered player to the game");
	}
	
	public PlayerAlreadyRegisteredException(Player p){
		super("Tried to add the player " + p.getPlayerName() + " [ AID : " + p.getAID().toString() +" ], who is already in the game");
	}
	
}
