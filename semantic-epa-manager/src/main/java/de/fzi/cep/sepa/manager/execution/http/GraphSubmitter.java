package de.fzi.cep.sepa.manager.execution.http;

import java.util.List;

import de.fzi.cep.sepa.model.InvocableSEPAElement;

public class GraphSubmitter {

	private List<InvocableSEPAElement> graphs;
	
	public GraphSubmitter(List<InvocableSEPAElement> graphs)
	{
		this.graphs = graphs;
	}
	
	public boolean invokeGraphs()
	{
		for(InvocableSEPAElement graph : graphs)
		{
			new HttpRequestBuilder(graph).invoke();
		}
		
		return true;
	}
	
	public boolean detachGraphs()
	{
		for(InvocableSEPAElement graph : graphs)
		{
			new HttpRequestBuilder(graph).detach();
		}
		
		return true;
	}
}
