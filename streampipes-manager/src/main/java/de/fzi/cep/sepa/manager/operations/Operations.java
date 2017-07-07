package de.fzi.cep.sepa.manager.operations;

import de.fzi.cep.sepa.commons.exceptions.NoSuitableSepasAvailableException;
import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.manager.endpoint.EndpointItemFetcher;
import de.fzi.cep.sepa.manager.execution.http.PipelineExecutor;
import de.fzi.cep.sepa.manager.execution.http.PipelineStorageService;
import de.fzi.cep.sepa.manager.matching.PipelineVerificationHandler;
import de.fzi.cep.sepa.manager.recommender.ElementRecommender;
import de.fzi.cep.sepa.manager.verification.extractor.TypeExtractor;
import de.fzi.cep.sepa.model.client.endpoint.RdfEndpoint;
import de.fzi.cep.sepa.model.client.endpoint.RdfEndpointItem;
import de.fzi.cep.sepa.model.client.messages.Message;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.model.client.pipeline.PipelineElementRecommendationMessage;
import de.fzi.cep.sepa.model.client.pipeline.PipelineModificationMessage;
import de.fzi.cep.sepa.model.client.pipeline.PipelineOperationStatus;

import java.util.List;


/**
 * class that provides several (partial) pipeline verification methods
 * 
 * @author riemer
 *
 */

public class Operations {



	public static PipelineModificationMessage validatePipeline(Pipeline pipeline, boolean isPartial) throws Exception {
		return validatePipeline(pipeline, isPartial, "");
	}

	/**
	 * This method is a fix for the streamsets integration. Remove the username from the signature when you don't need it anymore
	 * @param pipeline
	 * @param isPartial
	 * @param username
	 * @return
     * @throws Exception
     */
	public static PipelineModificationMessage validatePipeline(Pipeline pipeline, boolean isPartial, String username)
			throws Exception {
		PipelineVerificationHandler validator = new PipelineVerificationHandler(
				pipeline);
		return validator
		.validateConnection()
		.computeMappingProperties(username)
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
	
	public static PipelineElementRecommendationMessage findRecommendedElements(String email, Pipeline partialPipeline) throws NoSuitableSepasAvailableException
	{
		return new ElementRecommender(email, partialPipeline).findRecommendedElements();
	}

	public static void storePipeline(Pipeline pipeline) {
		new PipelineStorageService(pipeline).addPipeline();
	}

	public static PipelineOperationStatus startPipeline( 
			Pipeline pipeline) {
		return startPipeline(pipeline, true, true, false);
	}
	
	public static PipelineOperationStatus startPipeline(
			Pipeline pipeline, boolean visualize, boolean storeStatus, boolean monitor) {
		return new PipelineExecutor(pipeline, visualize, storeStatus, monitor).startPipeline();		
	}

	public static PipelineOperationStatus stopPipeline( 
			Pipeline pipeline) {
		return stopPipeline(pipeline, true, true, false);
	}


	public static PipelineOperationStatus stopPipeline(
			Pipeline pipeline, boolean visualize, boolean storeStatus, boolean monitor) {
		return new PipelineExecutor(pipeline, visualize, storeStatus, monitor).stopPipeline();
	}

	public static List<RdfEndpointItem> getEndpointUriContents(List<RdfEndpoint> endpoints) {
		return new EndpointItemFetcher(endpoints).getItems();
	}
}
