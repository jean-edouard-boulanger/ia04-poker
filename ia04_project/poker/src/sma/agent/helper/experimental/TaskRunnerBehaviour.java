package sma.agent.helper.experimental;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.util.leap.Collection;
import jade.util.leap.Iterator;

/**
 * This behavior wrap a task and will stop when the wrapped task is done.
 * 
 * the wrapped behavior had to set with the .setBehaviour() before the 
 * TaskRunnerBhv is started.
 * 
 * This behavior is internally based on a SequentialBehaviour with only 
 * one sub-behavior. The behavior try to overcome the WrapperBehaviour which is
 * very similar but doesn't allows to set the sub-behavior after the constructor.
 **/
public class TaskRunnerBehaviour extends SequentialBehaviour {

	private boolean isStarted;

	public TaskRunnerBehaviour(Agent agent){
		super(agent);
		isStarted = false;
	};

	@Override
	public void onStart() {
		this.isStarted = true;
		super.onStart();
	}
	
	public void setBehaviour(Behaviour bhv){
		/*if(this.isStarted){
			System.err.println("Error: can't set the sub-behaviour of an already started TaskRunnerBhv.");
		}
		else{
			this.addSubBehaviour(bhv);	
		}*/
		this.addSubBehaviour(bhv);
	}
}
