package sma.agent;

import gui.player.PlayerWindow;
import gui.server.ServerWindow;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFservicehelper;
import jade.core.Agent;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

public class HumanPlayerAgent extends GuiAgent {

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public void setup()
	{
		super.setup();
		DFservicehelper.registerService(this, "PokerSimulation","Simulation");
		
		PlayerWindow player_window = new PlayerWindow();
		player_window.setHumanPlayerAgent(this);
		changes.addPropertyChangeListener(player_window);

	}
	
	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
