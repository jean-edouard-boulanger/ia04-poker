package sma.agent.simulationAgent;

import jade.core.behaviours.Behaviour;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import sma.agent.SimulationAgent;
import jade.wrapper.AgentController;

public class WaitAIBehaviour extends Behaviour{
	
	public SimulationAgent simAgent;
	
	public WaitAIBehaviour(SimulationAgent simAgent){
		super(simAgent);
		this.simAgent = simAgent;
	}
	
	@Override
	public void onStart() {
		if(simAgent.getAddAIBeforeStarting()){
		
			ContainerController container = this.simAgent.getContainerController();
			
			try {
				for(int i = 0; i < simAgent.getMaxPlayers() - simAgent.getGame().getPlayersContainer().getPlayers().size(); ++i){
					AgentController ac = container.createNewAgent("IA" + i, "sma.agent.AIPlayerAgent", null);
					ac.start();
				}
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean done() {
		return !simAgent.getAddAIBeforeStarting() || simAgent.getMaxPlayers() == simAgent.getGame().getPlayersContainer().getPlayers().size();
	}
	
	@Override
	public void action() {
	}
}
