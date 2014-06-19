package sma.agent.simulationAgent;

import java.util.ArrayList;

import javax.jws.soap.SOAPBinding;

import poker.game.model.Game;
import poker.game.model.Round;
import poker.game.model.PlayersContainer.PlayerSmartIterator;
import poker.game.player.model.Player;
import sma.agent.SimulationAgent;
import sma.agent.SimulationAgent.GameEvent;
import sma.agent.helper.BlankBehaviour;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBehaviour;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;

/**
 * @author JE
 * This behaviour prepares a table round
 * It first gets the list of the players who are still playing (i.e. Not folded or out) thanks to the player container
 */
public class InitTableRoundBehaviour extends TaskRunnerBehaviour {
	
	SimulationAgent simulationAgent;
	
	public InitTableRoundBehaviour(SimulationAgent simulationAgent){
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
	}
	
	@Override
	public void onStart(){
		System.out.println("@@@ TableRoundBehaviour @@@");
		
		Task mainTask = Task.New(new BlankBehaviour());
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
			
			System.out.println("DEBUG [SimulationAgent:InitTableRound] " + playersInGame.size() + " will play in the next round");
		}
		
		for(Player player : playersInGame){
			mainTask = mainTask.then(new PlayBehaviour(this.simulationAgent, player.getAID()));
		}
		
		mainTask = mainTask.then(new OneShotBehaviour() {
			@Override
			public void action() {
				simulationAgent.allowNextPlayRequests();
			}
		});
		
		setBehaviour(mainTask);
		super.onStart();
	}
	
	public int onEnd(){
		return SimulationAgent.GameEvent.TABLE_ROUND_END.ordinal();
	}
}