package sma.agent;

import gui.server.ServerWindow;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import java.beans.PropertyChangeSupport;

import poker.game.model.Game;
import sma.agent.helper.DFServiceHelper;
import sma.agent.simAgent.CheckWinnerBhv;
import sma.agent.simAgent.GameEndedBhv;
import sma.agent.simAgent.InitGameBhv;
import sma.agent.simAgent.InitHandBhv;
import sma.agent.simAgent.InitRoundBhv;
import sma.agent.simAgent.PlayBhv;
import sma.agent.simAgent.PlayerSubscriptionBhv;

/**
 * Simulation agent.
 * This agent handle the main steps of the poker simulation as well as game configuration.
 *
 */
public class SimulationAgent extends GuiAgent {
	
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	private Game game;
	private int maxPlayers = 2; //TODO: synchronize this parameter with the server GUI.
	private boolean serverStarted = false;
	private boolean gameStarted = false;
	
	
	public enum GameEvent{NEW_HAND, NEW_ROUND, ROUND_ENDED, GAME_FINISHED, PLAY}
	
	@Override
	public void setup()
	{
		super.setup();
		DFServiceHelper.registerService(this, "PokerSimulation","Simulation");
		
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
			StartServer();
			break;
		case LAUNCH_GAME:
			StartGame();
			break;
		default:
			break;
		}
	}
	
	private void StartServer(){
		System.out.println("[" + this.getLocalName() + "] Server started, now waiting players...");
		this.serverStarted = true;
		//TODO: set game parameters (nb max players, chip distribution, blind augmentation interval, etc.)
	}
	
	private void StartGame(){
		System.out.println("[" + this.getLocalName() + "] Game started.");
		this.gameStarted = true;
		
		// we start the main Final State Machine behavior:
		FSMBehaviour gameBhv = new FSMBehaviour(this) {
			// we had this handler for debugging purpose.
			@Override
			protected void handleStateEntered(Behaviour state){
				super.handleStateEntered(state);
				System.out.println("[" + this.myAgent.getLocalName() + "] Round: " + this.getExecutionState());
			}
		};
		
		gameBhv.registerFirstState(new InitGameBhv(this), "Game init");
		gameBhv.registerState(new InitHandBhv(this), "Hand init");
		gameBhv.registerState(new InitRoundBhv(this), "Round init");
		gameBhv.registerState(new PlayBhv(this), "Play");
		gameBhv.registerState(new CheckWinnerBhv(this), "Check winner");
		gameBhv.registerLastState(new GameEndedBhv(this), "Game ended");
		
		gameBhv.registerTransition("Game init", "Hand init", GameEvent.NEW_HAND.ordinal());
		gameBhv.registerTransition("Hand init", "Round init", GameEvent.NEW_ROUND.ordinal());
		gameBhv.registerTransition("Round init", "Play", GameEvent.PLAY.ordinal());
		gameBhv.registerTransition("Play", "Play", GameEvent.PLAY.ordinal());
		gameBhv.registerTransition("Play", "Check winner", GameEvent.ROUND_ENDED.ordinal());
		gameBhv.registerTransition("Check winner", "Round init", GameEvent.NEW_ROUND.ordinal());
		gameBhv.registerTransition("Check winner", "Hand init", GameEvent.NEW_HAND.ordinal());
		gameBhv.registerTransition("Check winner", "Game ended", GameEvent.GAME_FINISHED.ordinal());
		
		addBehaviour(gameBhv);
		
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
