package sma.agent.simulationAgent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.agent.SimulationAgent;
import sma.agent.helper.AgentHelper;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.bet.request.BetRequest;
import sma.message.bet.request.FoldRequest;

public class CheckPlayersActionsBehaviour extends CyclicBehaviour {

	SimulationAgent simulationAgent;
	MessageTemplate messageTemplate;
	private CheckPlayersActionsBehaviourMessageVisitor messageVisitor;
	
	public CheckPlayersActionsBehaviour(SimulationAgent simulationAgent){
		super(simulationAgent);
		this.simulationAgent = simulationAgent;
		this.messageVisitor = new CheckPlayersActionsBehaviourMessageVisitor();
	}
	
	@Override
	public void action() {
		AID playerAllowedToBet = simulationAgent.getPlayerAllowedToBetAID();
		
		if(playerAllowedToBet == null){
			this.messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		}
		else {
			this.messageTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.not(MessageTemplate.MatchSender(playerAllowedToBet)));
		}
		
		boolean received = AgentHelper.receiveMessage(simulationAgent, this.messageTemplate, messageVisitor);
		if(!received){
			block();
		}
	}
	
	private class CheckPlayersActionsBehaviourMessageVisitor extends MessageVisitor{
		@Override
		public boolean onFoldRequest(FoldRequest request, ACLMessage aclMsg){
			System.err.println("WARNING [CheckPlayersActionsBehaviour] Fold request from player " + aclMsg.getSender().getLocalName() + " blocked");
			AgentHelper.sendReply(simulationAgent, aclMsg, ACLMessage.FAILURE, new FailureMessage("You currently cannot bet"));
			return true;
		}
		
		@Override
		public boolean onBetRequest(BetRequest request, ACLMessage aclMsg) {
			System.err.println("WARNING [CheckPlayersActionsBehaviour] Bet request from player " + request.getPlayerAID().getLocalName() + " blocked");
			AgentHelper.sendReply(simulationAgent, aclMsg, ACLMessage.FAILURE, new FailureMessage("You currently cannot bet"));
			return true;
		}
	}
}
