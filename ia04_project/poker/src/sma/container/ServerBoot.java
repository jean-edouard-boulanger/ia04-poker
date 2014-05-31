package sma.container;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;


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
			ContainerController container = rt.createAgentContainer(p);
			
			AgentController simulation = container.createNewAgent("Simulation", "sma.agent.SimAgent", null);
			AgentController environment = container.createNewAgent("Environment", "sma.agent.EnvAgent", null);
			AgentController croupier = container.createNewAgent("Croupier", "sma.agent.CroupierAgent", null);
			AgentController blindManagement = container.createNewAgent("blindManagement", "sma.agent.CroupierAgent", null);
			
			simulation.start();
			environment.start();
			croupier.start();
			blindManagement.start();
		} 
		catch (Exception ex) 
		{
			System.out.println("Error while starting server container: "  + ex.getLocalizedMessage());
		}	
	}
}
