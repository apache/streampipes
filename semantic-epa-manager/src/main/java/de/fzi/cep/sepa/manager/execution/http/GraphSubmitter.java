package de.fzi.cep.sepa.manager.execution.http;

import java.util.List;

import de.fzi.cep.sepa.messages.PipelineOperationStatus;
import de.fzi.cep.sepa.model.InvocableSEPAElement;

public class GraphSubmitter {

	private List<InvocableSEPAElement> graphs;
	private String pipelineId;
	
	public GraphSubmitter(String pipelineId, List<InvocableSEPAElement> graphs)
	{
		this.graphs = graphs;
		this.pipelineId = pipelineId;
	}
	
	public PipelineOperationStatus invokeGraphs()
	{
		PipelineOperationStatus status = new PipelineOperationStatus();
		status.setPipelineId(pipelineId);
		
		graphs.forEach(g -> status.addPipelineElementStatus(new HttpRequestBuilder(g).invoke()));
		status.setSuccess(!status.getElementStatus().stream().anyMatch(s -> !s.isSuccess()));
		
		return status;
	}
	
	public PipelineOperationStatus detachGraphs()
	{
		PipelineOperationStatus status = new PipelineOperationStatus();
		status.setPipelineId(pipelineId);
		
		graphs.forEach(g -> status.addPipelineElementStatus(new HttpRequestBuilder(g).detach()));
		status.setSuccess(!status.getElementStatus().stream().anyMatch(s -> !s.isSuccess()));
		
		return status;
	}
}
