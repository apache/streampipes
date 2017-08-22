package org.streampipes.rest.api;


import org.streampipes.model.client.pipeline.Pipeline;

import javax.ws.rs.core.Response;

public interface IPipeline extends IPipelineElement {
	
	Response addPipeline(String username, Pipeline pipeline);

	Response getSystemPipelines();

	Response start(String username, String pipelineId);

	Response stop(String username, String pipelineId);

	Response recommend(String email, Pipeline pipeline);

	Response update(Pipeline pipeline, String username);

	Response overwritePipeline(String username, Pipeline pipeline);

	Response getPipelineStatus(String username, String pipelineId);
}
