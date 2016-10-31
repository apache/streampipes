package de.fzi.cep.sepa.rest.api;


import de.fzi.cep.sepa.model.client.pipeline.Pipeline;

import javax.ws.rs.core.Response;

public interface IPipeline extends IPipelineElement {
	
	Response addPipeline(String username, Pipeline pipeline);

	Response getSystemPipelines();

	Response start(String username, String pipelineId);

	Response stop(String username, String pipelineId);

	Response recommend(Pipeline pipeline);

	Response update(Pipeline pipeline, String username);

	Response overwritePipeline(String username, Pipeline pipeline);

	Response getPipelineStatus(String username, String pipelineId);
}
