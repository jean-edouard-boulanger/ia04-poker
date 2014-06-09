package sma.agent;

import java.util.ArrayList;

import poker.game.model.BlindValueDefinition;
import poker.game.model.Game;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.NotificationSubscriber;
import sma.message.OKMessage;
import sma.message.blind.notification.BlindValueDefinitionUpdatedNotification;
import sma.message.blind.notification.TimeBeforeIncreasingBlindChangedNotification;
import sma.message.blind.request.ChangeTimeBeforeIncreasingBlindRequest;
import sma.message.blind.request.GetBlindValueDefinitionRequest;
import sma.message.environment.notification.BlindValueDefinitionChangedNotification;
import sma.message.environment.request.BlindValueDefinitionChangeRequest;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BlindManagementAgent extends Agent {
	
	private BlindIncreasingBehaviour blindIncreasingBehaviour;
	private int timeBeforeIncreasingBlind;
	private BlindValueDefinition blindValueDefinition;
		
	public BlindManagementAgent() {
		this.blindValueDefinition = new BlindValueDefinition();
	}
	
	@Override
	public void setup()
	{
		super.setup();
		DFServiceHelper.registerService(this, "BlindManagementAgent","BlindManager");
		
		this.addBehaviour(new ReceiveRequestBehaviour(this));
	}
	
	private class ReceiveRequestBehaviour extends CyclicBehaviour {
		public ReceiveRequestBehaviour(Agent agent) {
			super(agent);
		}

		@Override
		public void action() {
			if(!AgentHelper.receiveMessage(this.myAgent, ACLMessage.REQUEST, new MessageVisitor(){
				
				@Override
				public boolean onGetBlindValueDefinitionRequest(GetBlindValueDefinitionRequest request, ACLMessage aclMsg) {
					//Giving blind value to the environment
					AID environment = DFServiceHelper.searchService(myAgent, "PokerEnvironment", "Environment");

					//Transaction with environment
					TransactionBhv transaction = new TransactionBhv(myAgent, new BlindValueDefinitionChangeRequest(blindValueDefinition), environment);
					
					transaction.setResponseVisitor(new MessageVisitor(){
						@Override
						public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
							//Notifying the simulation that the environment got the new b
							AgentHelper.sendReply(BlindManagementAgent.this, aclMsg, ACLMessage.INFORM, new BlindValueDefinitionUpdatedNotification(blindValueDefinition));
							
							return true;
						}
					});
					
					return true;
				}
				
				//Changing time before increasing blind
				@Override
				public boolean onChangeTimeBeforeIncreasingBlindRequest(ChangeTimeBeforeIncreasingBlindRequest request, ACLMessage aclMsg) {
					
					timeBeforeIncreasingBlind = request.getTime();
					
					blindValueDefinition = new BlindValueDefinition(request.getTokenValueDefinition());
					
					myAgent.removeBehaviour(blindIncreasingBehaviour);
					
					myAgent.addBehaviour(new BlindIncreasingBehaviour(BlindManagementAgent.this, timeBeforeIncreasingBlind));
					
					AgentHelper.sendReply(BlindManagementAgent.this, aclMsg, ACLMessage.INFORM, new TimeBeforeIncreasingBlindChangedNotification(timeBeforeIncreasingBlind));
					
					return true;
				}
			})){
				block();
			}
		}
	}
	
	private class BlindIncreasingBehaviour extends TickerBehaviour{
		
		public BlindIncreasingBehaviour(Agent agent, int timeBeforeIncreasingBlind){
			super(agent, timeBeforeIncreasingBlind);
		}

		@Override
		protected void onTick() {
			blindValueDefinition.increase();
		}
	}

	public int getTimeBeforeIncreasingBlind() {
		return timeBeforeIncreasingBlind;
	}

	public void setTimeBeforeIncreasingBlind(int timeBeforeIncreasingBlind) {
		this.timeBeforeIncreasingBlind = timeBeforeIncreasingBlind;
	}

	public BlindValueDefinition getBlindValueDefinition() {
		return blindValueDefinition;
	}

	public void setBlindValueDefinition(BlindValueDefinition blindValueDefinition) {
		this.blindValueDefinition = blindValueDefinition;
	}
}
