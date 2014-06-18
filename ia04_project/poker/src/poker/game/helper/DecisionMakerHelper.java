package poker.game.helper;

import gui.player.event.model.PlayRequestEventData;

import java.util.ArrayList;

import poker.game.model.BetType;
import poker.game.model.Decision;
import poker.game.model.Round;

public class DecisionMakerHelper {	
	
	public static Decision makeDecision(PlayRequestEventData eventData, Round round) {
		
		ArrayList<BetType> betActions = eventData.getAvailableActions();
		
		
		
		
		return null;
	}
}
