package sma.agent.helper.experimental;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WrapperBehaviour;

import java.util.ArrayList;
import java.util.List;

/**
 * Behavior wrapper that support chaining action with .then() method (producing a
 * Sequential behavior) and with .whenAll()/.whenAny() methods (producing parallel
 * behaviors.
 * 
 * For instance (b1 to b6 are Behaviour objects):
 * 
 * 	Task.New(b1).then(b2).whenAll(b3, b4, b5).then(b6).run(agent);
 * 
 * is equivalent to:
 * 
 * 	SequencialBehviour seq1 = new SequencialBehaviour(agent);
 * 	seq1.addSubBehaviour(b1);
 * 	seq1.addSubBehaviour(b2);
 * 	ParallelBehaviour par = new ParallelBehaviour(agent, WHEN_ALL);
 * 	par.addSubBehaviour(b3);
 * 	par.addSubBehaviour(b4);
 * 	par.addSubBehaviour(b6);
 * 	seq1.addSubBehviour(b1);
 * 	seq1.addSubBehviour(b1);
 * 	agent.addBehaviour(seq1);
 * 
 * .doAll(...) and doAny(...) are used to create a task starting with a parallel behavior.
 * 
 * The method .parallel() provide another syntax to fill ParallelBehviour:
 * 
 * 	Task.New(b1).parallel().add(b2).add(b3).whenAll();  <=>  Task.New(b1).whenAll(b2, b3);
 * 
 * Remarks:  
 * 	- Wrapped task shall have their Agent property set.
 * 	- when .then(), .whenAny(), .whenAll(), .parallel() on a task 't1', the 't1' task 
 * 	  should not be used anymore and only the return task should be launch.
 * 
 **/
public class Task extends WrapperBehaviour {


    /** Main factory method **/
    static public Task New(Behaviour bhv){
	return new Task(bhv);
    }

    /**
     * Chain a task sequentially.
     *   /!\ this method modify the original task.
     */
    public Task then(Behaviour bhv){
	SequentialBehaviour seq = null;
	if(this.getWrappedBehaviour() instanceof SequentialBehaviour) {
	    seq = (SequentialBehaviour) this.getWrappedBehaviour();
	}
	else {
	    seq = new SequentialBehaviour(bhv.getAgent());
	    seq.addSubBehaviour(this.getWrappedBehaviour());
	}
	seq.addSubBehaviour(bhv);
	return Task.New(seq);
    }

    /**
     * Chain given tasks in parallel, following tasks are executed when all parallel 
     * tasks are done.
     */
    public Task whenAll(Behaviour...bhvs){
	return this.then(doAny(bhvs));
    }

    /**
     * Chain given tasks in parallel, following tasks are executed when any parallel 
     * tasks is done.
     */
    public Task whenAny(Behaviour...bhvs){
	return this.then(doAll(bhvs));
    }


    /**
     * Create a new task starting with parallel tasks, following tasks are executed 
     * when all parallel tasks are done.
     */
    public static Task doAny(Behaviour...bhvs){
	if (bhvs.length == 0)
	    return Task.Empty();
	Agent agent = bhvs[0].getAgent();
	ParallelBehaviour parallel = new ParallelBehaviour(agent, ParallelBehaviour.WHEN_ALL);
	for (Behaviour bhv : bhvs){
	    parallel.addSubBehaviour(bhv);
	}
	return Task.New(parallel);
    }

    /**
     * Create a new task starting with parallel tasks, following tasks are executed 
     * when any parallel tasks is done.
     */
    public static Task doAll(Behaviour...bhvs){
	if (bhvs.length == 0)
	    return Task.Empty();
	Agent agent = bhvs[0].getAgent();
	ParallelBehaviour parallel = new ParallelBehaviour(agent, ParallelBehaviour.WHEN_ANY);
	for (Behaviour bhv : bhvs){
	    parallel.addSubBehaviour(bhv);
	}
	return Task.New(parallel);
    }

    /**
     * Obtain a Parallel object.
     */
    public Parallel parallel(){
	return new Parallel(this);
    }

    /**
     * Run the task in the given agent.
     */
    public Task run(Agent agent){
	this.setAgent(agent);
	agent.addBehaviour(this);
	return this;
    }


    // direct construction disabled
    private Task(Behaviour bhv){
	super(bhv);
    }

    // Create an empty task doing nothing (used internally)
    private static Task Empty(){
	return new Task(new OneShotBehaviour() {@Override public void action() {}});
    }

    /**
     * Represent a set of behaviors to be started in parallel with .whenAny() 
     * or .whenAll() methods.
     */
    public static class Parallel{
	private Task previousTask = null;
	private List<Behaviour> parallelBhvs;

	private Parallel(Task previousTask){
	    this.previousTask = previousTask;
	    this.parallelBhvs = new ArrayList<Behaviour>();
	}

	private Parallel(){
	    this(null);
	}

	public Parallel add(Behaviour bhv){
	    parallelBhvs.add(bhv);
	    return this;
	}

	public Task whenAll(){
	    if (parallelBhvs.size() == 0)
		return Task.Empty();
	    Agent agent = parallelBhvs.get(0).getAgent();
	    ParallelBehaviour parallel = new ParallelBehaviour(agent, ParallelBehaviour.WHEN_ALL);
	    for (Behaviour bhv : parallelBhvs){
		parallel.addSubBehaviour(bhv);
	    }
	    if(previousTask != null)
		return previousTask.then(parallel);
	    else
		return Task.New(parallel);
	}
	
	public Task whenAny(){
	    if (parallelBhvs.size() == 0)
		return Task.Empty();
	    Agent agent = parallelBhvs.get(0).getAgent();
	    ParallelBehaviour parallel = new ParallelBehaviour(agent, ParallelBehaviour.WHEN_ANY);
	    for (Behaviour bhv : parallelBhvs){
		parallel.addSubBehaviour(bhv);
	    }
	    if(previousTask != null)
		return previousTask.then(parallel);
	    else
		return Task.New(parallel);
	}

	/**
	 * Create a new Parralel used to create parallel tasks.
	 */
	public static Parallel New(){
	    return new Parallel();
	}

    }
}
