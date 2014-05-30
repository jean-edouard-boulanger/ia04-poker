package server;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class ServerBoot {

	public static String SECONDARY_PROPERTIES_FILE = "second_container.property";
	
	public static void main(String[] args) {
		
		if(args.length > 0)
			SECONDARY_PROPERTIES_FILE = args[0];
		
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		try {
			p = new ProfileImpl(SECONDARY_PROPERTIES_FILE);
			ContainerController cc = rt.createAgentContainer(p);
			
			AgentController EnvAgent = cc.createNewAgent("Environnement", "sma.EnvAgent", null);
			EnvAgent.start();
			
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}	
	}
}
