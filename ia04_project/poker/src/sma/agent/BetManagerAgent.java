package sma.agent;

import jade.core.Agent;

import com.fasterxml.jackson.databind.Module.SetupContext;

import poker.game.model.BetContainer;
import poker.game.model.PlayersContainer;
import poker.token.model.TokenValueDefinition;
import sma.message.MessageVisitor;

public class BetManagerAgent extends Agent {

	BetContainer betContainer;
	PlayersContainer playersContainer;
	
	MessageVisitor betManagerMessageVisitor;
	
	public BetManagerAgent(){
		super();
	}
	
	public void setup(){
		
	}
	
	private class BetManagerMessageVisitor extends MessageVisitor{	
		
	}
}
