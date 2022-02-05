package io.github.contractautomata.OrchestrationExample;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import io.github.contractautomata.OrchestrationExample.principals.Alice;
import io.github.contractautomata.OrchestrationExample.principals.Bob;
import io.github.contractautomata.RunnableOrchestration.RunnableOrchestratedContract;
import io.github.contractautomata.RunnableOrchestration.RunnableOrchestration;
import io.github.contractautomata.RunnableOrchestration.actions.DistributedOrchestratedAction;
import io.github.contractautomata.RunnableOrchestration.actions.DistributedOrchestratorAction;
import io.github.contractautomata.RunnableOrchestration.impl.MajoritarianChoiceRunnableOrchestratedContract;
import io.github.contractautomata.RunnableOrchestration.impl.MajoritarianChoiceRunnableOrchestration;
import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.transition.Transition;
import io.github.davidebasile.contractautomata.converters.DataConverter;
import io.github.davidebasile.contractautomata.requirements.Agreement;

public class App {
	private final static String dir = System.getProperty("user.dir")+File.separator
			+"resources"+File.separator;

	public static void main(String[] args) throws IOException {

		// the designer of the application creates the  requirement
		//substitute with = createNewRequirement(); to change the application behaviour
		Automaton<String, String, BasicState,Transition<String, String, BasicState,Label<String>>> req = createNewRequirement();
		System.out.println("Requirement : \n" + req.toString());


		//the services providers publish their contracts and services, in this example everything is local
		MSCA ca = new DataConverter().importMSCA(dir+"Alice.data");
		RunnableOrchestratedContract alice = new MajoritarianChoiceRunnableOrchestratedContract(ca,8080, new Alice(), new DistributedOrchestratedAction());
		new Thread(alice).start();

		MSCA cb = new DataConverter().importMSCA(dir+"Bob.data");
		RunnableOrchestratedContract bob = new MajoritarianChoiceRunnableOrchestratedContract(cb,8082, new Bob(),  new DistributedOrchestratedAction());
		new Thread(bob).start();


		// when the hosts and ports running the threads alice and bob are discovered, 
		// the runnable orchestration can be built and, if not empty, executed
		RunnableOrchestration ron = new MajoritarianChoiceRunnableOrchestration(req,new Agreement(),
				Arrays.asList(alice.getContract(),bob.getContract()),
				Arrays.asList(null,null), 
				Arrays.asList(alice.getPort(),bob.getPort()),
				new DistributedOrchestratorAction());
		
		if (ron.isEmptyOrchestration())
			System.out.println("No orchestration found");
		else
			new Thread(ron).start();

		

	}

	/**
	 * just an example of how it is possible to hard-code an automaton rather than loading it from a file.
	 * 
	 * @return the requirement
	 */
	@SuppressWarnings("unused")
	private static  Automaton<String, String, BasicState,Transition<String, String, BasicState,Label<String>>>  createNewRequirement() {
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,false);
		BasicState s2 = new BasicState("2",false,true);
		Transition<String, String, BasicState,Label<String>> t1 = new Transition<>(s0, new Label<String>("euro"), s1);
		Transition<String, String, BasicState,Label<String>> t2 = new Transition<>(s1, new Label<String>("coffee"), s2);
		return new Automaton<>(Set.of(t1,t2));
	}

	/**
	 * just an example of how it is possible to hard-code an automaton rather than loading it from a file.
	 * 
	 * @return the requirement
	 */
	@SuppressWarnings("unused")
	private static  Automaton<String, String, BasicState,Transition<String, String, BasicState,Label<String>>>  createOldRequirement() {
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,false);
		BasicState s2 = new BasicState("3",false,false);
		BasicState s3 = new BasicState("2",false,true);
		Transition<String, String, BasicState,Label<String>> t1 = new Transition<>(s0, new Label<String>("euro"), s1);
		Transition<String, String, BasicState,Label<String>> t2 = new Transition<>(s1, new Label<String>("coffee"), s3);
		Transition<String, String, BasicState,Label<String>> t3 = new Transition<>(s0, new Label<String>("dollar"), s2);
		Transition<String, String, BasicState,Label<String>> t4 = new Transition<>(s2, new Label<String>("tea"), s3);
		return new Automaton<>(Set.of(t1,t2,t3,t4));
	}

}
