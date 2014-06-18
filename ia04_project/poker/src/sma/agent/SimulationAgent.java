package sma.agent;

import gui.server.ServerWindow;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;

import poker.card.heuristics.combination.model.Hand;
import poker.game.model.Game;
import poker.game.model.Round;
import poker.game.player.model.Player;
import poker.token.exception.InvalidRepartitionException;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.exception.InvalidTokenValueException;
import poker.token.factories.TokenSetFactory;
import poker.token.model.TokenRepartition;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import poker.token.model.TokenValueDefinition;
import sma.agent.helper.DFServiceHelper;
import sma.agent.simulationAgent.EndRoundBehaviour;
import sma.agent.simulationAgent.EndTableRoundBehaviour;
import sma.agent.simulationAgent.EnvironmentWatcherBehaviour;
import sma.agent.simulationAgent.InitGameBehaviour;
import sma.agent.simulationAgent.InitHandBehaviour;
import sma.agent.simulationAgent.InitRoundBehaviour;
import sma.agent.simulationAgent.InitTableRoundBehaviour;
import sma.agent.simulationAgent.PlayerSubscriptionBehaviour;

/**
 * Simulation agent.
 * This agent handle the main steps of the poker simulation as well as game configuration.
 *
 */
public class SimulationAgent extends GuiAgent {

	public enum GameEvent{
		NEW_HAND, 
		NEW_TABLE_ROUND,
		TABLE_ROUND_END,
		NEW_ROUND, 
		END_ROUND,
		GAME_FINISHED,
		FIND_HAND_WINNERS,
		END_HAND
	}

	private static final String INIT_GAME = "INIT_GAME";
	private static final String INIT_HAND = "INIT_HAND";
	private static final String INIT_ROUND = "INIT_ROUND";
	private static final String TABLE_ROUND = "TABLE_ROUND";
	private static final String END_TABLE_ROUND = "TABLE_ROUND_END";
	private static final String END_ROUND = "END_ROUND";
	private static final String FIND_HAND_WINNERS = "FIND_HAND_WINNERS";

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	private Game game;
	private int maxPlayers = 2;
	private boolean serverStarted = false;
	private boolean gameStarted = false;
	private TokenSet defaultTokenSet;
	private int blindIncreaseDelayS;
	private TokenValueDefinition defaultTokenValueDefinition;

	private HashMap<Player, Hand> winners;

	private boolean cancelNextPlayRequests = false;
	private Round currentRound;
	private int roundTableNumber = 0;

	private AID playerAllowedToBetAID;

	public SimulationAgent(){
		super();
		this.winners = new HashMap<Player, Hand>();
	}

	@Override
	public void setup()
	{
		super.setup();
		DFServiceHelper.registerService(this, "PokerSimulation", "Simulation");

		ServerWindow server_window = new ServerWindow(this);
		changes.addPropertyChangeListener(server_window);

		this.game = new Game();

		// we create a default token distribution:
		try {
			defaultTokenValueDefinition = new TokenValueDefinition();

			defaultTokenValueDefinition.setValueForTokenType(TokenType.WHITE, 1);
			defaultTokenValueDefinition.setValueForTokenType(TokenType.RED, 5);
			defaultTokenValueDefinition.setValueForTokenType(TokenType.GREEN, 10);
			defaultTokenValueDefinition.setValueForTokenType(TokenType.BLUE, 25);
			defaultTokenValueDefinition.setValueForTokenType(TokenType.BLACK, 50);

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

		addBehaviour(new PlayerSubscriptionBehaviour(this));
		addBehaviour(new EnvironmentWatcherBehaviour(this));
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
			//int distribNb = (Integer)evt.getParameter(2);
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
		FSMBehaviour gameBehaviour = new FSMBehaviour(this) {
			// we had this handler for debugging purpose.
			@Override
			protected void handleStateEntered(Behaviour state){

				// Creates a new instance of the current state behaviour dynamically
				try {
					String name = this.getName(state);
					state = state.getClass().getConstructor(SimulationAgent.class).newInstance(SimulationAgent.this);
					this.registerState(state, name);
				} catch (Exception e) 
				{
					e.printStackTrace();
				}

				System.out.println("-------------------------------------------------------");
				System.out.println("[" + this.myAgent.getLocalName() + "] Current state: " + this.getName(state));
				System.out.println("-------------------------------------------------------");

				super.handleStateEntered(state);
			}
		};

		gameBehaviour.registerFirstState(new InitGameBehaviour(this), INIT_GAME);
		gameBehaviour.registerState(new InitHandBehaviour(this), INIT_HAND);
		gameBehaviour.registerState(new InitRoundBehaviour(this), INIT_ROUND);
		gameBehaviour.registerState(new InitTableRoundBehaviour(this), TABLE_ROUND);
		gameBehaviour.registerState(new EndTableRoundBehaviour(this), END_TABLE_ROUND);
		gameBehaviour.registerState(new EndRoundBehaviour(this), END_ROUND);


		gameBehaviour.registerTransition(INIT_GAME, INIT_HAND, GameEvent.NEW_HAND.ordinal());
		gameBehaviour.registerTransition(INIT_HAND, INIT_ROUND, GameEvent.NEW_ROUND.ordinal());
		gameBehaviour.registerTransition(INIT_ROUND, TABLE_ROUND, GameEvent.NEW_TABLE_ROUND.ordinal());
		gameBehaviour.registerTransition(INIT_ROUND, END_ROUND, GameEvent.END_ROUND.ordinal());
		gameBehaviour.registerTransition(TABLE_ROUND, END_TABLE_ROUND, GameEvent.TABLE_ROUND_END.ordinal());
		gameBehaviour.registerTransition(END_TABLE_ROUND, TABLE_ROUND, GameEvent.NEW_TABLE_ROUND.ordinal());
		gameBehaviour.registerTransition(END_TABLE_ROUND, END_ROUND, GameEvent.END_ROUND.ordinal());
		gameBehaviour.registerTransition(END_ROUND, INIT_ROUND, GameEvent.NEW_ROUND.ordinal());

		addBehaviour(gameBehaviour);
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

	public int getBlindIncreaseDelayS() {
		return this.blindIncreaseDelayS;
	}

	public TokenValueDefinition getDefaultTokenValueDefinition() {
		return this.defaultTokenValueDefinition;
	}

	public void setCurrentRound(Round round) {
		currentRound = round;
	}

	public Round getCurrentRound() {
		return currentRound;
	}

	public void setRoundTableNumber(int roundTableNumber){
		this.roundTableNumber = roundTableNumber;
	}

	public int getRoundTableNumber(){
		return this.roundTableNumber;
	}

	public int nextRoundTableNumber(){
		return ++this.roundTableNumber;
	}

	public void resetRoundTableNumber(){
		this.roundTableNumber = 1;
	}

	public void setPlayerAllowedToBetAID(AID playerAID){
		this.playerAllowedToBetAID = playerAID;
	}

	public AID getPlayerAllowedToBetAID(){
		return this.playerAllowedToBetAID;
	}

	public void cancelNextPlayRequests(){
		this.cancelNextPlayRequests = true;
	}

	public void allowNextPlayRequests(){
		this.cancelNextPlayRequests = false;
	}

	public boolean arePlayRequestsCancelled(){
		return this.cancelNextPlayRequests;
	}

	public void resetWinners(){
		this.winners.clear();
	}
	
	public HashMap<Player, Hand> getWinners() {
		return winners;
	}

	public void setWinners(HashMap<Player, Hand> winners) {
		this.winners = winners;
	}
	
	public void addWinner(Player p, Hand h) {
		this.winners.put(p, h);
	}
}