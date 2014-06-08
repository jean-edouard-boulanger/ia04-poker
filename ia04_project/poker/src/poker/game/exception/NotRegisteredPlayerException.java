package poker.game.exception;

import poker.game.player.model.Player;

public class NotRegisteredPlayerException extends Exception {

	public NotRegisteredPlayerException(){
		super("The specified player is not reggistered in the game");
	}
	
	public NotRegisteredPlayerException(Player p){
		super("The player " + p.getPlayerName() + " [ AID: " + p.getAID().toString() + " ] is not registered in the game");
	}
	
}
