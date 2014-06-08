package sma.container;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;


/**
 * 
 * Server startup class, create a container with all agents but players.
 *	
 */
public class ServerBoot {

	public static String PROPERTY_FILE = "ServerContainer.property";
	
	/**
	 * 
	 * @param args	If an argument is provided, it's used as a path to the Jade container
	 * 				property file, otherwise default file is loaded (defined in PROPERTY_FILE);
	 */
	public static void main(String[] args) {
		
		if(args.length > 0)
			PROPERTY_FILE = args[0];
		
		try {
			Runtime rt = Runtime.instance();
			ProfileImpl p = new ProfileImpl(PROPERTY_FILE);
			AgentContainer container = rt.createMainContainer(p);
			
			AgentController simulation = container.createNewAgent("Simulation", "sma.agent.SimulationAgent", null);
			AgentController environment = container.createNewAgent("Environment", "sma.agent.EnvironmentAgent", null);
			AgentController croupier = container.createNewAgent("Croupier", "sma.agent.DealerAgent", null);
			AgentController blindManagement = container.createNewAgent("blindManagement", "sma.agent.BlindManagementAgent", null);
			
			simulation.start();
			environment.start();
			croupier.start();
			blindManagement.start();
		} 
		catch (Exception ex) 
		{
			System.out.println("Error while starting server container: "  + ex.getMessage());
		}	
	}
}
