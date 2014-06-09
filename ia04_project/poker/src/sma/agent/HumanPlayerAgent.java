package sma.agent;

import gui.player.PlayerWindow;
import gui.server.ServerWindow;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.PlayerSubscriptionRequest;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFHSQLKB;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class HumanPlayerAgent extends GuiAgent {

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public void setup()
	{
		super.setup();

		// WTF ?
		//DFServiceHelper.registerService(this, "PokerSimulation","HumanPlayer");
		
		PlayerWindow player_window = new PlayerWindow();
		player_window.setHumanPlayerAgent(this);
		changes.addPropertyChangeListener(player_window);
		
		// player subscription for debugging purpose:
		AID simulation = DFServiceHelper.searchService(this, "PokerSimulation","Simulation");
		this.addBehaviour(new TransactionBhv(this, new PlayerSubscriptionRequest(generateNickName()), simulation, ACLMessage.SUBSCRIBE));
	}
	
	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub	
	}
	
	
	private static String generateNickName(){
		//TODO: To be improved...
		return Long.toHexString(Double.doubleToLongBits(Math.random()));
	}
}
