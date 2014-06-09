package poker.game.exception;

import poker.game.player.model.Player;

public class TablePlaceNotAvailableException extends Exception {
	
	public TablePlaceNotAvailableException() {
		super("The required place is currently not available");
	}
	
	public TablePlaceNotAvailableException(Player p){
		super("The required place ( " + p.getTablePositionIndex() + " ) is not available, the player " + p.getPlayerName() + " [AID: " + p.getAID().toString() + "] is already sitting");
	}
	
}
