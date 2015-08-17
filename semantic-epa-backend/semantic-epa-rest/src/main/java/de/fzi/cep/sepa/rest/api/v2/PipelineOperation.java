package de.fzi.cep.sepa.rest.api.v2;

import de.fzi.cep.sepa.model.client.Pipeline;

public interface PipelineOperation extends SepaElementOperation{
	
	public String start(String pipelineId);
	
	public String stop(String pipelineId);
	
	public String recommend(Pipeline pipeline);
	
	public String update(Pipeline pipeline);
}
