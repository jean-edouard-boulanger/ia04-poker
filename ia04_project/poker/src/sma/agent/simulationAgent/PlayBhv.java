package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import sma.agent.SimulationAgent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.SimpleVisitor;
import sma.agent.helper.TransactionBhv;
import sma.agent.helper.experimental.Task;
import sma.agent.helper.experimental.TaskRunnerBhv;
import sma.message.Message;
import sma.message.MessageVisitor;
import sma.message.bet.request.BetRequest;
import sma.message.simulation.request.PlayRequest;

/**
 * - ask current player to play
 * - check player move (if check or bet)
 * - modify environment (if fold or all-in)
 * - check if the round is done 
 * - increment current player
 */
public class PlayBhv extends TaskRunnerBhv {
	
	private SimulationAgent simAgent;
	private AID betManager;
	
	public PlayBhv(SimulationAgent simAgent) {
		super(simAgent);
		this.simAgent = simAgent;
		this.betManager = DFServiceHelper.searchService(simAgent, "BetManagerAgent", "BetManager");
	}

	@Override
	public void onStart() {
		
		Task mainTask = Task.New(playBhv())
				.then(checkIfRoundDone());
		
		this.setBehaviour(mainTask);
		super.onStart();
	}
	
	private Behaviour playBhv(){
		Message msg = new PlayRequest();
		AID currPlayer = simAgent.getGame().getPlayersContainer().getCurrentPlayer().getAID();
		TransactionBhv transaction = new TransactionBhv(myAgent, msg, currPlayer);
		transaction.setResponseVisitor(new MessageVisitor(){
			
			@Override
			public boolean onBetRequest(BetRequest request, ACLMessage aclMsg) {
				
				TransactionBhv transactionBetRequestBehaviour = new TransactionBhv(simAgent, request, betManager, ACLMessage.REQUEST);
				
				transactionBetRequestBehaviour.setResponseVisitor(new SimpleVisitor(myAgent,
						"[Simulation] Player " + request.getPlayerAID() + " bet.",
						"[Simulation] Player " + request.getPlayerAID() + " could not bet."));

				simAgent.addBehaviour(transactionBetRequestBehaviour);
				return true;
			}
			
			/*@Override
			public boolean onBetRequest(BetRequest request, ACLMessage aclMsg) {
				
				TransactionBhv transactionBetRequestBehaviour = new TransactionBhv(simAgent, request, betManager, ACLMessage.REQUEST);
				
				transactionBetRequestBehaviour.setResponseVisitor(new SimpleVisitor(myAgent,
						"[Simulation] Player " + request.getPlayerAID() + " bet.",
						"[Simulation] Player " + request.getPlayerAID() + " could not bet."));

				simAgent.addBehaviour(transactionBetRequestBehaviour);
				return true;
			}*/
			
			//TODO: fold
			//TODO: all-in		
			
		});		
		return transaction;
	}
	
	private Behaviour checkIfRoundDone(){

		while(true); // !!!!!!!!!! temporary !!!!!!!!!!!!
		
		/*Message msg = new PlayRequest(round);
		TransactionBhv transaction = new TransactionBhv(myAgent, msg, dealerAgent);
		transaction.setResponseVisitor(new SimpleVisitor(myAgent,
				"community cards dealt successfully.",
				"error while dealing community cards."));		
		return transaction;*/
		//return null;
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
