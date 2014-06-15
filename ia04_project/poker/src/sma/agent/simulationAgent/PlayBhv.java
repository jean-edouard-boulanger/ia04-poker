package sma.agent.simulationAgent;

import jade.core.behaviours.Behaviour;
import sma.agent.SimulationAgent;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBhv;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBhv;
import sma.message.Message;
import sma.message.dealer.request.DealRequest;

/**
 * - ask current player to play
 * - check player move (if check or bet)
 * - modify environment (if fold or all-in)
 * - check if the round is done 
 * - increment current player
 */
public class PlayBhv extends TaskRunnerBhv {
	
	private SimulationAgent simAgent;

	public PlayBhv(SimulationAgent simAgent) {
		super(simAgent);
		this.simAgent = simAgent;
	}

	@Override
	public void onStart() {
		
		while(true); // !!!!!!!!!! temporary !!!!!!!!!!!!

		//Task mainTask = Task.New(playBhv())
		//		.then(checkIfRoundDone());
		
		//this.setBehaviour(mainTask);
		//super.onStart();
	}
	
	private Behaviour playBhv(){
		/*Message msg = new PlayRequest(round);
		TransactionBhv transaction = new TransactionBhv(myAgent, msg, dealerAgent);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"community cards dealt successfully.",
				"error while dealing community cards."));		
		return transaction;*/
		return null;
	}
	
	private Behaviour checkIfRoundDone(){
		/*Message msg = new PlayRequest(round);
		TransactionBhv transaction = new TransactionBhv(myAgent, msg, dealerAgent);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"community cards dealt successfully.",
				"error while dealing community cards."));		
		return transaction;*/
		return null;
	}
	
	
	
	/**
	 * Transition: 
	 * Return the transition code, either PLAY or STEP_ENDED.
	 */
	@Override
	public int onEnd(){
		if(checkIfRoundFinished())
			return SimulationAgent.GameEvent.ROUND_ENDED.ordinal();
		else
			return SimulationAgent.GameEvent.PLAY.ordinal();
		
	}
	
	/**
	 * Check if the current round (pre-flop, flop, river or turn) is finished meaning
	 * that everyone called or folded (except for the pre-flop where the big blind can either choose
	 * to check or to raise after every called or folded).
	 * @return true if the round is finished.
	 */
	private boolean checkIfRoundFinished() {
		// TODO
		return false;
	}

}
