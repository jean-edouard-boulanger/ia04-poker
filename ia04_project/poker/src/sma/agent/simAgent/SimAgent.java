package sma.agent.simAgent;

import gui.server.ServerWindow;
import jade.core.behaviours.SequentialBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import java.beans.PropertyChangeSupport;

import poker.game.model.Game;
import sma.agent.helper.DFservicehelper;

/**
 * Simulation agent.
 * This agent handle the main steps of the poker simulation as well as game configuration.
 *
 */
public class SimAgent extends GuiAgent {
	
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	private Game game;
	private int maxPlayers = 2; //TODO: synchronize this parameter with the server GUI.
	private boolean serverStarted = false;
	private boolean gameStarted = false;
	
	@Override
	public void setup()
	{
		super.setup();
		DFservicehelper.registerService(this, "PokerSimulation","Simulation");
		
		ServerWindow server_window = new ServerWindow(this);
		changes.addPropertyChangeListener(server_window);
		
		addBehaviour(new PlayerSubscriptionBhv(this));
	}
	
	/**
	 * Handle events from the GUI
	 */
	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		switch (ServerWindow.ServerGuiEvent.values()[arg0.getType()]) {
		case LAUNCH_SERVER:
			System.out.println("[" + this.getLocalName() + "] Server started, now waiting players ...");
			this.serverStarted = true;
			//TODO: set game parameters (nb max players, chip distribution, blind augmentation interval, etc.)
			break;
		case LAUNCH_GAME:
			System.out.println("[" + this.getLocalName() + "] Game started.");
			this.gameStarted = true;
			break;
		default:
			break;
		}
	}
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public boolean isServerStarted() {
		return serverStarted;
	}

	public void setServerStarted(boolean serverStarted) {
		this.serverStarted = serverStarted;
	}

	public boolean isGameStarted() {
		return gameStarted;
	}

	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}
	
}
