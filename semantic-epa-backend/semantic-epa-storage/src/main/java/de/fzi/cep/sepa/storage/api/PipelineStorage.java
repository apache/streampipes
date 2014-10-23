package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.model.client.Pipeline;

public interface PipelineStorage {

	public List<Pipeline> getAllPipelines();
	
	public void storePipeline(Pipeline pipeline);
	
	public void updatePipeline(Pipeline pipeline);
	
	public Pipeline getPipeline(String pipelineId);
	
	public void deletePipeline(String pipelineId);
	
	public <T> void store(T object);
}
