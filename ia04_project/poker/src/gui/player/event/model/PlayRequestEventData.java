package gui.player.event.model;

import java.util.ArrayList;

import poker.game.model.BetType;

public class PlayRequestEventData {

	ArrayList<BetType> availableActions;
	
	int minimumBetAmount = 0;
	int maximumBetAmount = 0;
	int raiseAmount = 0;
		
	boolean requestResentFollowedToError = false;
	String errorMessage;
	
	public PlayRequestEventData(){
		this.availableActions = new ArrayList<BetType>();
		this.errorMessage = null;
	}

	public ArrayList<BetType> getAvailableActions() {
		return availableActions;
	}

	public void setAvailableActions(ArrayList<BetType> availableActions) {
		this.availableActions = availableActions;
	}

	public void addAvailableAction(BetType action){
		if(!availableActions.contains(action)){
			this.availableActions.add(action);
		}
	}
	
	public void removeAvailableAction(BetType action){
		availableActions.remove(action);
	}
		
	public void addAllAvailableActions(){
		this.availableActions.clear();
		for(BetType t : BetType.values()){
			this.availableActions.add(t);
		}
	}
	
	public void clearAvailableActions(){
		this.availableActions.clear();
	}
	
	public int getMinimumBetAmount() {
		return minimumBetAmount;
	}

	public void setMinimumBetAmount(int minimumBetAmount) {
		this.minimumBetAmount = minimumBetAmount;
	}

	public boolean isRequestResentFollowedToError() {
		return requestResentFollowedToError;
	}

	public void setRequestResentFollowedToError(boolean requestResentFollowedToError) {
		this.requestResentFollowedToError = requestResentFollowedToError;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public int getMaximumBetAmount() {
		return maximumBetAmount;
	}

	public void setMaximumBetAmount(int maximumBetAmount) {
		this.maximumBetAmount = maximumBetAmount;
	}

	public int getRaiseAmount() {
		return raiseAmount;
	}

	public void setRaiseAmount(int raiseAmount) {
		this.raiseAmount = raiseAmount;
	}	
}
