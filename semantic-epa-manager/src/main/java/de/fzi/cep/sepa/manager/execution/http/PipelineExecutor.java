package de.fzi.cep.sepa.manager.execution.http;

import java.util.Date;
import java.util.List;

import org.lightcouch.DocumentConflictException;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.manager.matching.InvocationGraphBuilder;
import de.fzi.cep.sepa.manager.matching.TreeBuilder;
import de.fzi.cep.sepa.manager.util.TemporaryGraphStorage;
import de.fzi.cep.sepa.messages.PipelineOperationStatus;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.RunningVisualization;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class PipelineExecutor {

	private Pipeline pipeline;
	
	public PipelineExecutor(Pipeline pipeline)
	{
		this.pipeline = pipeline;
	}
	
	public PipelineOperationStatus startPipeline()
	{
		GenericTree<NamedSEPAElement> tree = new TreeBuilder(pipeline).generateTree(false);
		InvocationGraphBuilder builder = new InvocationGraphBuilder(tree, false, pipeline.getPipelineId());
		List<InvocableSEPAElement> graphs = builder.buildGraph();
		
		SecInvocation sec = getSECInvocationGraph(graphs);
		RunningVisualization viz = new RunningVisualization(pipeline.getPipelineId(), pipeline.getName(), sec.getBelongsTo() + "/" +sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName(), sec.getDescription(), sec.getName());
		StorageManager.INSTANCE.getPipelineStorageAPI().storeVisualization(viz);
		storeInvocationGraphs(pipeline.getPipelineId(), graphs);
		
		PipelineOperationStatus status = new GraphSubmitter(pipeline.getPipelineId(), graphs).invokeGraphs();
		
		if (status.isSuccess()) 
		{
			setPipelineStarted(pipeline);
		}
		return status;
	}
	
	public PipelineOperationStatus stopPipeline()
	{
		List<InvocableSEPAElement> graphs = TemporaryGraphStorage.graphStorage.get(pipeline.getPipelineId());
		PipelineOperationStatus status = new GraphSubmitter(pipeline.getPipelineId(), graphs).detachGraphs();
		
		if (status.isSuccess())
		{
			StorageManager.INSTANCE.getPipelineStorageAPI().deleteVisualization(pipeline.getPipelineId());
			setPipelineStopped(pipeline);
		}
		return status;
	}
	
	private void setPipelineStarted(de.fzi.cep.sepa.model.client.Pipeline pipeline) {
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
	
	private void setPipelineStopped(de.fzi.cep.sepa.model.client.Pipeline pipeline) {
		pipeline.setRunning(false);
		StorageManager.INSTANCE.getPipelineStorageAPI().updatePipeline(pipeline);
	}
	
	private void storeInvocationGraphs(String pipelineId, List<InvocableSEPAElement> graphs)
	{
		TemporaryGraphStorage.graphStorage.put(pipelineId, graphs);
	}
	
	private SecInvocation getSECInvocationGraph(List<InvocableSEPAElement> graphs)
	{
		for (InvocableSEPAElement graph : graphs)
			if (graph instanceof SecInvocation) return (SecInvocation) graph;
		throw new IllegalArgumentException("No action element available");
	}
}
