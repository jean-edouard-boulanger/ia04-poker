package sma.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import poker.game.model.BlindValueDefinition;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.blind.request.GetBlindValueDefinitionRequest;
import sma.message.blind.request.ResetBlindRequest;
import sma.message.environment.request.BlindValueDefinitionChangeRequest;

public class BlindManagementAgent extends Agent {
	
	private BlindIncreasingBehaviour blindIncreasingBehaviour;
	private BlindValueDefinition blindValueDefinition;
		
	public BlindManagementAgent() {
		this.blindValueDefinition = new BlindValueDefinition();
		this.blindIncreasingBehaviour = null;
	}
	
	@Override
	public void setup()
	{
		super.setup();
		DFServiceHelper.registerService(this, "BlindManagementAgent","BlindManager");
		this.addBehaviour(new ReceiveRequestBehaviour(this));
	}
	
	private class ReceiveRequestBehaviour extends CyclicBehaviour {
		private AID environment;

		public ReceiveRequestBehaviour(Agent agent) {
			super(agent);
			environment = DFServiceHelper.searchService(myAgent, "PokerEnvironment", "Environment");
		}

		@Override
		public void action() {
			
			boolean msgReceived = AgentHelper.receiveMessage(this.myAgent, ACLMessage.REQUEST, new MessageVisitor(){
				
				@Override
				public boolean onRefreshBlindValueDefinitionRequest(GetBlindValueDefinitionRequest request, ACLMessage aclMsg) {
					setBlindAndReply(aclMsg); // we refresh the environment and reply to simulation
					return true;
				}
				
				//Changing time before increasing blind
				@Override
				public boolean onResetBlindRequest(ResetBlindRequest request, ACLMessage aclMsg) {
					
					blindValueDefinition = new BlindValueDefinition(request.getTokenValueDefinition());
					if(blindIncreasingBehaviour != null)
						myAgent.removeBehaviour(blindIncreasingBehaviour);
					blindIncreasingBehaviour = new BlindIncreasingBehaviour(BlindManagementAgent.this, request.getTime());
					myAgent.addBehaviour(blindIncreasingBehaviour);
					
					// the blind was reset, so we have to notify the environment:
					setBlindAndReply(aclMsg);
					
					return true;
				}
			});
			
			if(!msgReceived)
				block();
		}
		
		private void setBlindAndReply(final ACLMessage requestAclMsg){
			//Transaction with environment
			TransactionBhv transaction = new TransactionBhv(myAgent, new BlindValueDefinitionChangeRequest(blindValueDefinition), environment);
			transaction.setResponseVisitor(new MessageVisitor(){
				@Override
				public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg){ 
					// reset operation done, we reply to simulation:
					AgentHelper.sendReply(myAgent, requestAclMsg, ACLMessage.INFORM, new OKMessage());		
					return true;
				}
				
				@Override
				public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
					AgentHelper.sendReply(myAgent, requestAclMsg, ACLMessage.FAILURE, new FailureMessage(msg.getMessage()));		
					return true;
				}
			});
			
			// we start the transaction
			myAgent.addBehaviour(transaction);
		}
	}
	
	private class BlindIncreasingBehaviour extends TickerBehaviour{
		
		public BlindIncreasingBehaviour(Agent agent, int timeBeforeIncreasingBlind){
			super(agent, timeBeforeIncreasingBlind * 1000);
		}

		@Override
		protected void onTick() {
			blindValueDefinition.increase();
			System.out.println("[" + myAgent.getLocalName() + "] blind increased internally, now at: [" 
					+ blindValueDefinition.getBigBlindAmountDefinition() + ", " + blindValueDefinition.getBlindAmountDefinition() + "] (environment not yet updated).");
		}
	}
}
