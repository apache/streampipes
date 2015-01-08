package de.fzi.cep.sepa.manager.pipeline;

import java.util.List;

import de.fzi.cep.sepa.manager.http.HttpRequestBuilder;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;

public class GraphSubmitter {

	private List<SEPAInvocationGraph> graphs;
	
	public GraphSubmitter(List<SEPAInvocationGraph> graphs)
	{
		this.graphs = graphs;
	}
	
	public boolean invokeGraphs()
	{
		for(SEPAInvocationGraph graph : graphs)
		{
			System.out.println("Topic: " +graph.getOutputStream().getEventGrounding().getTopicName());
			new HttpRequestBuilder(graph).invoke();
		}
		
		return true;
	}
	
	public static boolean detachGraphs()
	{
		return true;
	}
}
