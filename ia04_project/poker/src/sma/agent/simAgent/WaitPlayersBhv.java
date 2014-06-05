package sma.agent.simAgent;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import poker.game.player.model.AIPlayer;
import poker.game.player.model.HumanPlayer;
import poker.game.player.model.Player;
import sma.agent.helper.AgentHelper;
import sma.message.FailureMessage;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.PlayerSubscriptionRequest;

/**
 * This behavior wait player subscriptions.
 * The behavior stop when the user click 'Start the game' in the server gui.
 */
public class WaitPlayersBhv extends Behaviour
{
	private SimAgent simAgent;
	
	public WaitPlayersBhv(SimAgent agent){
		super(agent);
		this.simAgent = agent;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		System.out.println(simAgent.getLocalName() + " now waiting players ...");
	};
	
	/**
	 * Register players if there is enough room.
	 * Stop when the gameStart.
	 */
	@Override
	public void action() {
			
		boolean msgReceived = AgentHelper.receiveMessage(this.myAgent, ACLMessage.SUBSCRIBE, new MessageVisitor(){
			@Override
			public boolean onPlayerSubscriptionRequest(PlayerSubscriptionRequest request, ACLMessage aclMsg){
				if(simAgent.getGame().getGamePlayers().size() < simAgent.getMaxPlayers()){
					
					// TODO: maybe we don't need to differentiate AI players from Human player in this agent
					Player player = null;
					if(request.isHuman())
						player = new HumanPlayer();
					else
						player = new AIPlayer();
					
					player.setPlayerName(request.getPlayerName());
					player.setId(aclMsg.getSender());
					simAgent.getGame().getGamePlayers().add(player);
					
					AgentHelper.sendReply(simAgent, aclMsg, ACLMessage.INFORM, new OKMessage());
				}
				else{ // No more player are allowed, we send a failure msg:
					AgentHelper.sendReply(simAgent, aclMsg, ACLMessage.FAILURE, new FailureMessage("Server not ready."));
				}
				return true;
			}
		});
		
		//if(!msgReceived)
		//	block();
		
	}

	@Override
	public boolean done() {
		return simAgent.isGameStarted();
	}
	
}