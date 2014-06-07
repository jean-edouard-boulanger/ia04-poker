package sma.agent.helper;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DFservicehelper {

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
			fe.printStackTrace();
		}
	}

}
