package de.fzi.cep.sepa.manager.operations;

import de.fzi.cep.sepa.commons.exceptions.NoSuitableSepasAvailableException;
import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.manager.execution.http.PipelineExecutor;
import de.fzi.cep.sepa.manager.matching.PipelineValidationHandler;
import de.fzi.cep.sepa.manager.recommender.ElementRecommender;
import de.fzi.cep.sepa.manager.verification.extractor.TypeExtractor;
import de.fzi.cep.sepa.messages.Message;
import de.fzi.cep.sepa.messages.PipelineModificationMessage;
import de.fzi.cep.sepa.messages.PipelineOperationStatus;
import de.fzi.cep.sepa.messages.RecommendationMessage;
import de.fzi.cep.sepa.model.client.Pipeline;

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
		.storeConnection()
		.getPipelineModificationMessage();
	}
	
	public static Message verifyAndAddElement(String graphData, String username) throws SepaParseException
	{
		return verifyAndAddElement(graphData, username, false);
	}
	
	public static Message verifyAndAddElement(String graphData, String username, boolean publicElement) throws SepaParseException
	{
		return new TypeExtractor(graphData).getTypeVerifier().verifyAndAdd(username, publicElement);
	}
	
	public static Message verifyAndUpdateElement(String graphData, String username) throws SepaParseException
	{
		return new TypeExtractor(graphData).getTypeVerifier().verifyAndUpdate(username);
	}
	
	public static RecommendationMessage findRecommendedElements(Pipeline partialPipeline) throws NoSuitableSepasAvailableException
	{
		return new ElementRecommender(partialPipeline).findRecommendedElements();
	}

	public static PipelineOperationStatus startPipeline(
			de.fzi.cep.sepa.model.client.Pipeline pipeline) {
		return new PipelineExecutor(pipeline).startPipeline();		
	}

	public static PipelineOperationStatus stopPipeline(
			de.fzi.cep.sepa.model.client.Pipeline pipeline) {
		return new PipelineExecutor(pipeline).stopPipeline();
	}
}
