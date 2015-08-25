package de.fzi.cep.sepa.rest.api.v2;


public interface PipelineOperation extends SepaElementOperation {
	
	public String addPipeline(String username, String pipeline);
	
	public String start(String username, String pipelineId);
	
	public String stop(String username, String pipelineId);
	
	public String recommend(String pipeline);
	
	public String update(String pipeline);
}
