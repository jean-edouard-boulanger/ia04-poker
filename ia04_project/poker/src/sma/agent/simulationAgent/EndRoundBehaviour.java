package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;

import java.util.ArrayList;

import poker.card.heuristics.combination.model.Hand;
import poker.game.model.Round;
import poker.game.player.model.Player;
import sma.agent.SimulationAgent;
import sma.agent.SimulationAgent.GameEvent;
import sma.agent.helper.BlankBehaviour;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBehaviour;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBehaviour;
import sma.message.Message;
import sma.message.bet.request.MergeBetsRequest;

public class EndRoundBehaviour extends TaskRunnerBehaviour {

	SimulationAgent simulationAgent;
	AID betMenager;

	GameEvent transition = GameEvent.NEW_ROUND;
	
	public EndRoundBehaviour(SimulationAgent simulationAgent) {
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
		this.betMenager = DFServiceHelper.searchService(simulationAgent, "BetManagerAgent", "BetManager");
	}
	
	@Override
	public void onStart(){
		Round round = this.simulationAgent.getCurrentRound();
		Task mainTask = Task.New(new BlankBehaviour());
		
		mainTask = mainTask.then(getMergeBetsBehaviour(getBetMenager()));
		
		if(round == Round.PLAYER_CARDS_DEAL){
			/*
			 * If the cards are dealt at the current round, we set the current round to PRE_FLOP, and we go back to init round
			 */
			simulationAgent.setCurrentRound(Round.PREFLOP);
		}
		else
		{
			/*
			 * Otherwise, we check if there is more than one player remaining in the table
			 */
			//int nbRemainingPlayers = this.simulationAgent.getGame().getPlayersContainer().getPlayersInGame(first)
			ArrayList<Player> remainingPlayers = this.simulationAgent.getGame().getPlayersContainer().getPlayersInGame();
			if(remainingPlayers.size() > 1){
				/*
				 * If there is more than one player remaining, we jump to the next round (Except if we are on the turn)
				 */
				
				Round currentRound = simulationAgent.getCurrentRound();
				
				this.transition = GameEvent.NEW_ROUND;
				if(currentRound == Round.RIVER){
					this.transition = GameEvent.SHOW_DOWN;
				}

				simulationAgent.setCurrentRound(currentRound.getNext());
			}
			else 
			{
				/*
				 * Otherwise, the remaining player is the hand winner
				 */
				this.simulationAgent.setCurrentRound(Round.SHOWDOWN);
				this.simulationAgent.addWinner(remainingPlayers.get(0).getAID(), null);
				this.transition = GameEvent.END_HAND;
			}
		}
		this.setBehaviour(mainTask);
		super.onStart();
	}
	
	private Behaviour getMergeBetsBehaviour(AID betManager) {
		Message msg = new MergeBetsRequest();
		TransactionBehaviour transaction = new TransactionBehaviour(myAgent, msg, betManager);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"Bets merged in pot.",
				"Bets could not be merged in pot."));		
		return transaction;
	}
	
	public AID getBetMenager() {
		return betMenager;
	}
	
	@Override
	public int onEnd(){
		return this.transition.ordinal();
	}
}
