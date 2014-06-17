package sma.agent.simulationAgent;

import java.util.ArrayList;

import javax.jws.soap.SOAPBinding;

import poker.game.model.Game;
import poker.game.model.Round;
import poker.game.model.PlayersContainer.PlayerSmartIterator;
import poker.game.player.model.Player;
import sma.agent.SimulationAgent;
import sma.agent.SimulationAgent.GameEvent;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBhv;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;

/**
 * @author JE
 * This behaviour prepares a table round
 * It first gets the list of the players who are still playing (i.e. Not folded or out) thanks to the player container
 */
public class TableRoundBehaviour extends TaskRunnerBhv {
	
	SimulationAgent simulationAgent;
	
	public TableRoundBehaviour(SimulationAgent simulationAgent){
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
	}
	
	@Override
	public void onStart(){
		System.out.println("@@@ TableRoundBehaviour @@@");
		
		Task mainTask = Task.New(new DoNothingBehaviour(simulationAgent));
		ArrayList<Player> playersInGame = null;
		
		Game game = this.simulationAgent.getGame();
		
		if(this.simulationAgent.getCurrentRound() == Round.PREFLOP){
			// If we are in Pre-Flop, the first player to bet is the one next to the big blind
			Player playerNextToBigBlind = game.getPlayersContainer().getPlayerNextTo(game.getPlayersContainer().getBigBlind());
			playersInGame = game.getPlayersContainer().getPlayersInGame(playerNextToBigBlind);
		}
		else {
			// Otherwise, the first player to bet is the one next to the dealer (i.e, the small blind)
			Player smallBlind = game.getPlayersContainer().getSmallBlind();
			playersInGame = game.getPlayersContainer().getPlayersInGame(smallBlind);
		}
		
		for(Player player : playersInGame){
			System.out.println("[TableRoundBehaviour] Player index on the table: " + player.getTablePositionIndex());
			mainTask = mainTask.then(new PlayBehaviour(this.simulationAgent, player.getAID()));
		}
		
		setBehaviour(mainTask);
		super.onStart();
	}
	
	public int onEnd(){
		this.reset();
		return SimulationAgent.GameEvent.TABLE_ROUND_END.ordinal();
	}
	
	private class DoNothingBehaviour extends OneShotBehaviour{
		
		public DoNothingBehaviour(SimulationAgent agent){
			super(agent);
		}
		
		@Override
		public void action() {}
	}	
}