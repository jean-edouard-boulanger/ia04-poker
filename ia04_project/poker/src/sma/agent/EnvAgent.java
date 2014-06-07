package sma.agent;

import gui.server.ServerWindow;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import poker.card.model.CommunityCards;
import poker.game.model.BlindValueDefinition;
import poker.game.player.model.Player;
import poker.token.model.TokenValueDefinition;
import sma.agent.helper.DFservicehelper;
import sma.agent.simAgent.PlayerSubscriptionBhv;

public class EnvAgent extends Agent {
	
	private ArrayList<Player> players;
	private CommunityCards communityCards;
	private BlindValueDefinition blindValueDefinition;
	private TokenValueDefinition tokenValueDefinition;
	private int currentPlayerIndex;
	
	@Override
	public void setup()
	{
		super.setup();
		DFservicehelper.registerService(this, "PokerEnvironment","Environment");
	}
	
	
	private Player getCurrentPlayer(){
		return this.players.get(currentPlayerIndex);
	}
	
	private Player getPlayerByAID(AID aid){
		for(Player p : this.players){
			if(p.getAID().equals(aid)){
				return p;
			}
		}
		return null;
	}
	
	private Player setCurrentPlayerIndex(int currentPlayerIndex){
		this.currentPlayerIndex = currentPlayerIndex;
		return this.getCurrentPlayer();
	}
}
