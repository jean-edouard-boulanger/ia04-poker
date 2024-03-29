package sma.agent.helper;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DFServiceHelper {

	/**
	 * Register a service to the default DFservice
	 * @param name	name of the service to register
	 * @param type	type of the service to register
	 * For now, behavior is undefined if this function is called twice.
	 */
	public static void registerService(Agent agent, String name, String type){
		
		//TODO: allows the creation of severals service by successive call of this function
		// 		we have to check in there is already a DFAgentDescription in the DFService
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(agent.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(name);
		sd.setType(type);
		dfd.addServices(sd);
		try {
			DFService.register(agent, dfd);
		}
		catch (FIPAException fe) {
			//TODO: handle errors properly.
			fe.printStackTrace();
		}
	}
	
	
	/**
	 * Search for an agent having the requested service.
	 * @param agent	The agent whose searching for a service.
	 * @param name	Name of the service.
	 * @param type	Type of the service.
	 * @return 	The first agent AID matching requirements or null if the 
	 * 			DF is not accessible or if no agent were found.
	 */
	public static AID searchService(Agent agent, String name, String type) {
		try {			
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(type);
			sd.setName(name);
			template.addServices(sd);
			DFAgentDescription[] result = DFService.search(agent, template);
			if (result.length < 0)
				throw new Exception("No service available.");
			return result[0].getName();
		} 
		catch(Exception e) { 
			System.out.println("[" + agent.getLocalName() + "] Error while searching service in the DF (service type : " + type + ", service name : " + name + ") : " + e.getMessage());
			return null;
		}
	}

}
