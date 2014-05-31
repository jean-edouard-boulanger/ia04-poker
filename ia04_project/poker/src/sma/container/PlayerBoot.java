package sma.container;

import java.util.Random;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;


/**
 * 
 * Start a container with an human player agent.
 *	
 */
public class PlayerBoot {

	public static String PROPERTY_FILE = "PlayerContainer.property";
	
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
			ProfileImpl profile = new ProfileImpl(PROPERTY_FILE);
			ContainerController container = rt.createAgentContainer(profile);
			
			AgentController player = container.createNewAgent("Player" + (new Random()).nextInt(), "sma.agent.CroupierAgent", null);
			
			player.start();
		} 
		catch (Exception ex) 
		{
			System.out.println("Error while starting server container: "  + ex.getLocalizedMessage());
		}	
	}
}
