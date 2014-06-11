package sma.agent;

import gui.server.ServerWindow;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import java.beans.PropertyChangeSupport;

import poker.game.model.BlindValueDefinition;
import poker.game.model.Game;
import poker.token.exception.InvalidRepartitionException;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.exception.InvalidTokenValueException;
import poker.token.factories.TokenSetFactory;
import poker.token.model.TokenRepartition;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import poker.token.model.TokenValueDefinition;
import sma.agent.helper.DFServiceHelper;
import sma.agent.simulationAgent.CheckWinnerBhv;
import sma.agent.simulationAgent.EnvironmentWatcherBhv;
import sma.agent.simulationAgent.GameEndedBhv;
import sma.agent.simulationAgent.InitGameBhv;
import sma.agent.simulationAgent.InitHandBhv;
import sma.agent.simulationAgent.InitRoundBhv;
import sma.agent.simulationAgent.PlayBhv;
import sma.agent.simulationAgent.PlayerSubscriptionBhv;

/**
 * Simulation agent.
 * This agent handle the main steps of the poker simulation as well as game configuration.
 *
 */
public class SimulationAgent extends GuiAgent {
	
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	private Game game;
	private int maxPlayers = 2;
	private boolean serverStarted = false;
	private boolean gameStarted = false;
	private TokenSet defaultTokenSet;
	private int blindIncreaseDelayS;
	private TokenValueDefinition defaultTokenValueDefinition;
	
	public enum GameEvent{NEW_HAND, NEW_ROUND, ROUND_ENDED, GAME_FINISHED, PLAY}
	
	@Override
	public void setup()
	{
		super.setup();
		DFServiceHelper.registerService(this, "PokerSimulation","Simulation");
		
		ServerWindow server_window = new ServerWindow(this);
		changes.addPropertyChangeListener(server_window);
		
		this.game = new Game();
		
		// we create a default token distribution:
		try {
			defaultTokenValueDefinition = new TokenValueDefinition();
			defaultTokenValueDefinition.setValueForTokenType(TokenType.GREEN, 10);
			defaultTokenValueDefinition.setValueForTokenType(TokenType.BLACK, 1);
			defaultTokenValueDefinition.setValueForTokenType(TokenType.BLUE, 5);
			defaultTokenValueDefinition.setValueForTokenType(TokenType.WHITE, 25);
			defaultTokenValueDefinition.setValueForTokenType(TokenType.RED, 50);
		} catch (InvalidTokenValueException e) {
			e.printStackTrace();
		}
		try {
			TokenRepartition defaultTokenRepartiton = new TokenRepartition();
			defaultTokenRepartiton.setRepartitionForToken(TokenType.GREEN, 30);
			defaultTokenRepartiton.setRepartitionForToken(TokenType.BLACK, 30);
			defaultTokenRepartiton.setRepartitionForToken(TokenType.BLUE, 20);
			defaultTokenRepartiton.setRepartitionForToken(TokenType.WHITE, 10);
			defaultTokenRepartiton.setRepartitionForToken(TokenType.RED, 10);
			
			int nbTokens = 40;
			this.defaultTokenSet = TokenSetFactory.createTokenSet(defaultTokenRepartiton, nbTokens);
			
		} catch (InvalidTokenAmountException e) {
			e.printStackTrace();
		} catch(InvalidRepartitionException e){
			e.printStackTrace();
		}
		
		addBehaviour(new PlayerSubscriptionBhv(this));
		addBehaviour(new EnvironmentWatcherBhv(this));
	}
	
	/**
	 * Handle events from the GUI
	 */
	@Override
	protected void onGuiEvent(GuiEvent evt) {
		switch (ServerWindow.ServerGuiEvent.values()[evt.getType()]) {
		case LAUNCH_SERVER:
			//TODO: handle properly parameters.
			this.maxPlayers = (Integer)evt.getParameter(0);
			this.blindIncreaseDelayS = (Integer)evt.getParameter(1)*60;
			int distribNb = (Integer)evt.getParameter(2);
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
				System.out.println("[" + this.myAgent.getLocalName() + "] Current state: " + this.getName(state));
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

	public TokenSet getDefaultTokenSet() {
		return defaultTokenSet;
	}

	/**
	 * @return	the blind increase delay in seconds
	 */
	public int getBlindIncreaseDelayS() {
		return this.blindIncreaseDelayS;
	}

	public TokenValueDefinition getDefaultTokenValueDefinition() {
		return this.defaultTokenValueDefinition;
	}
	
}
