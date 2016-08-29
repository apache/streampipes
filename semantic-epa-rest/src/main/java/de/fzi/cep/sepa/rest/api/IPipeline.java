package de.fzi.cep.sepa.rest.api;


public interface IPipeline extends IPipelineElement {
	
	String addPipeline(String username, String pipeline);
	
	String start(String username, String pipelineId);
	
	String stop(String username, String pipelineId);
	
	String recommend(String pipeline);
	
	String update(String pipeline);
	
	String overwritePipeline(String username, String pipeline);

	String getPipelineStatus(String username, String pipelineId);
}
