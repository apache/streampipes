package de.fzi.cep.sepa.manager.operations;

import de.fzi.cep.sepa.manager.pipeline.PipelineValidationHandler;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.PipelineModificationMessage;

/**
 * class that provides several (partial) pipeline verification methods
 * 
 * @author riemer
 *
 */

public class Operations {

	public static PipelineModificationMessage validatePipeline(Pipeline pipeline, boolean isPartial)
			throws Exception {
		PipelineValidationHandler validator = new PipelineValidationHandler(
				pipeline, isPartial);
		return validator
		.validateConnection()
		.computeMappingProperties()
		.computeMatchingProperties()
		.validateProperties()
		.getPipelineModificationMessage();
	}
	
}
