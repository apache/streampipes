package de.fzi.cep.sepa.manager.operations;

import java.util.Date;
import java.util.List;

import org.lightcouch.DocumentConflictException;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.manager.pipeline.GraphSubmitter;
import de.fzi.cep.sepa.manager.pipeline.InvocationGraphBuilder;
import de.fzi.cep.sepa.manager.pipeline.PipelineValidationHandler;
import de.fzi.cep.sepa.manager.pipeline.TreeBuilder;
import de.fzi.cep.sepa.manager.util.TemporaryGraphStorage;
import de.fzi.cep.sepa.messages.PipelineModificationMessage;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.RunningVisualization;
import de.fzi.cep.sepa.model.impl.graph.SECInvocationGraph;
import de.fzi.cep.sepa.storage.controller.StorageManager;

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
		.getPipelineModificationMessage();
	}

	public static void startPipeline(
			de.fzi.cep.sepa.model.client.Pipeline pipeline) {
		
		GenericTree<NamedSEPAElement> tree = new TreeBuilder(pipeline).generateTree(false);
		InvocationGraphBuilder builder = new InvocationGraphBuilder(tree, false);
		List<InvocableSEPAElement> graphs = builder.buildGraph();
		
		SECInvocationGraph sec = getSECInvocationGraph(graphs);
		RunningVisualization viz = new RunningVisualization(pipeline.getPipelineId(), pipeline.getName(), sec.getBelongsTo() + "/" +sec.getInputStreams().get(0).getEventGrounding().getTopicName(), sec.getDescription(), sec.getName());
		StorageManager.INSTANCE.getPipelineStorageAPI().storeVisualization(viz);
		storeInvocationGraphs(pipeline.getPipelineId(), graphs);
		new GraphSubmitter(graphs).invokeGraphs();
		
		setPipelineStarted(pipeline);
		
	}
	
	private static void setPipelineStarted(de.fzi.cep.sepa.model.client.Pipeline pipeline) {
		System.out.println("Updating pipeline: " +pipeline.getName());
		pipeline.setRunning(true);
		pipeline.setStartedAt(new Date().getTime());
		try {
			StorageManager.INSTANCE.getPipelineStorageAPI().updatePipeline(pipeline);
		} catch (DocumentConflictException dce)
		{
			//dce.printStackTrace();
		}
	}
	
	private static void setPipelineStopped(de.fzi.cep.sepa.model.client.Pipeline pipeline) {
		pipeline.setRunning(false);
		StorageManager.INSTANCE.getPipelineStorageAPI().updatePipeline(pipeline);
	}
	
	private static void storeInvocationGraphs(String pipelineId, List<InvocableSEPAElement> graphs)
	{
		TemporaryGraphStorage.graphStorage.put(pipelineId, graphs);
	}
	
	private static SECInvocationGraph getSECInvocationGraph(List<InvocableSEPAElement> graphs)
	{
		for (InvocableSEPAElement graph : graphs)
			if (graph instanceof SECInvocationGraph) return (SECInvocationGraph) graph;
		throw new IllegalArgumentException("No action element available");
	}

	public static void stopPipeline(
			de.fzi.cep.sepa.model.client.Pipeline pipeline) {
		List<InvocableSEPAElement> graphs = TemporaryGraphStorage.graphStorage.get(pipeline.getPipelineId());
		try {
			new GraphSubmitter(graphs).detachGraphs();
		} catch (Exception e)
		{
			System.err.println("Error: Could not detach graphs.");
		}
		
		StorageManager.INSTANCE.getPipelineStorageAPI().deleteVisualization(pipeline.getPipelineId());
		setPipelineStopped(pipeline);
	}
	
}
