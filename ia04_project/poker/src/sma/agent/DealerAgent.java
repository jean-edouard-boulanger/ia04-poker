package sma.agent;

import java.util.ArrayList;

import poker.game.player.model.Player;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

public class DealerAgent extends Agent {

	ArrayList<Player> players;
	
	private class ReceiveEnvironmentNotificationBehaviour extends Behaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	private class DealCardsBehaviour extends Behaviour{

		@Override
		public void action() {
			
		}

		@Override
		public boolean done() {
			return false;
		}
		
	}
	
}
