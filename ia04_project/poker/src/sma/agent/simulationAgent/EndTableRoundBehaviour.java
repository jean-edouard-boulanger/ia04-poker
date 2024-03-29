package sma.agent.simulationAgent;

import sma.agent.SimulationAgent;
import sma.agent.SimulationAgent.GameEvent;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.RequestTransaction;
import sma.message.BooleanMessage;
import sma.message.MessageVisitor;
import sma.message.bet.request.AreBetsClosedRequest;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

/**
 * This behaviour is used to check if:
 * - There is only one player remaining => Transition to the behaviour that manages the end of a hand
 * - The bets are closed after a table round => Transition to the behaviour that manages the rounds of a hand 
 */
public class EndTableRoundBehaviour extends Behaviour {

	GameEvent endTransition = GameEvent.END_ROUND;
	SimulationAgent simulationAgent;
	private int step = 1;
	
	AID betManagerAID;
	RequestTransaction requestTransaction;
	
	public EndTableRoundBehaviour(SimulationAgent simulationAgent){
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
		this.betManagerAID = DFServiceHelper.searchService(this.simulationAgent, "BetManagerAgent","BetManager");
	}
	
	@Override
	public void action() {		
		if(this.step == 1)
		{			
			System.out.println("DEBUG [Simulation.TableRoundEndBehaviour:" + step + "] Asking the betManager whether the bets are closed");
			AreBetsClosedRequest request = new AreBetsClosedRequest();
			this.requestTransaction = new RequestTransaction(this, request, this.betManagerAID);
			this.requestTransaction.sendRequest();
			
			this.step = 2;
		}
		else if(step == 2)
		{
			System.out.println("DEBUG [Simulation.TableRoundEndBehaviour:" + step + "] Waiting response from the BetManager agent");
			boolean received = this.requestTransaction.checkReply(new TableRoundEndBehaviourMessageVisitor());
			if(!received){
				block();
			}
		}
		else 
		{
			System.out.println("DEBUG [Simulation.TableRoundEndBehaviour:" + step + "] DONE");
			step = 3;
		}
	}

	@Override
	public boolean done() {
		return step >= 3;
	}
	
	@Override
	public int onEnd(){
		return this.endTransition.ordinal();
	}
	
	private class TableRoundEndBehaviourMessageVisitor extends MessageVisitor{
		
		@Override
		public boolean onBooleanMessage(BooleanMessage message, ACLMessage aclMsg) {
			if(message.getValue()){
				System.out.println("DEBUG [Simulation.TableRoundEndBehaviour:" + step + "] Response received from the BetManagerAgent: TRUE => END_ROUND");
				endTransition = GameEvent.END_ROUND;
			}
			else {
				System.out.println("DEBUG [Simulation.TableRoundEndBehaviour:" + step + "] Response received from the BetManagerAgent: FALSE => NEW_TABLE_ROUND");
				endTransition = GameEvent.NEW_TABLE_ROUND;
			}
			
			step = 3;
			return true;
		}
		
	}
}
