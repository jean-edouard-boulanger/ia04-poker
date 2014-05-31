package sma.agent;

import gui.server.ServerWindow;

import java.beans.PropertyChangeSupport;

import poker.game.model.Game;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SimAgent extends GuiAgent {
	
	private Game game;
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public static final int LAUNCH_SERVER = 0;
	public static final int LAUNCH_GAME = 1;
	
	
	public void RegisterService(String name, String type){
		
		//TODO: allows the creation of severals service by successive call of this function
		// 		we have to check in there is already a DFAgentDescription in the DFService
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();

		sd.setName(name);
		sd.setType(type);
		
		dfd.addServices(sd);
				
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	public void setup()
	{
		super.setup();
		
		RegisterService("PokerEnvironment","Environment");
		
		ServerWindow server_window = new ServerWindow(this);
		changes.addPropertyChangeListener(server_window);
		
		addBehaviour(new listenLaunchServer());
	}
	
	public class listenLaunchServer extends Behaviour
	{
		
		private boolean done = false;
		
		@Override
		public void action() {
			MessageTemplate mti = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
					MessageTemplate.MatchConversationId("souscription"));
			
			ACLMessage message = myAgent.receive(mti);
			if (message != null) {
				
				try 
				{
					/*SubscriptionOperation operation = SubscriptionOperation.fromJson(message.getContent());
					SimAgent.this.list_aid.add(operation.getAid());*/
				} 
				catch (Exception e) 
				{
					System.out.println("[EnvAgent] Error while souscription received : " + e.getMessage());
					e.printStackTrace();
				}
				
			}
			else
			{
				block();
			}
			
		}

		@Override
		public boolean done() {
			return done;
		}
		
	}
	
	public class launchServer extends OneShotBehaviour
	{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}
		
	}

	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		if(arg0.getType() == LAUNCH_SERVER)
		{
			
		}
		
	}
}
