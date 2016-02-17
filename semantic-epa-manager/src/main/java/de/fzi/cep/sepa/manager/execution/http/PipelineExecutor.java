package de.fzi.cep.sepa.manager.execution.http;

import java.util.Date;
import java.util.List;

import org.lightcouch.DocumentConflictException;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
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
	private boolean visualize;
	private boolean storeStatus;
	
	public PipelineExecutor(Pipeline pipeline, boolean visualize, boolean storeStatus)
	{
		this.pipeline = pipeline;
		this.visualize = visualize;
		this.storeStatus = storeStatus;
	}
	
	public PipelineOperationStatus startPipeline()
	{
		GenericTree<NamedSEPAElement> tree = new TreeBuilder(pipeline).generateTree(false);
		InvocationGraphBuilder builder = new InvocationGraphBuilder(tree, false, pipeline.getPipelineId());
		List<InvocableSEPAElement> graphs = builder.buildGraph();
		
		SecInvocation sec = getSECInvocationGraph(graphs);
		
		PipelineOperationStatus status = new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(), graphs).invokeGraphs();
		
		if (status.isSuccess()) 
		{
			String uri = sec.getUri();
			if (ClientConfiguration.INSTANCE.isNissatechRunning()) uri = uri.replaceFirst("[a-zA-Z]{4}://[a-zA-Z0-9\\-\\.]+:\\d+", "http://proasense.nissatech.com/actions");
			RunningVisualization viz = new RunningVisualization(pipeline.getPipelineId(), pipeline.getName(), uri, sec.getDescription(), sec.getName());
			if (visualize) StorageManager.INSTANCE.getPipelineStorageAPI().storeVisualization(viz);
			storeInvocationGraphs(pipeline.getPipelineId(), graphs);
			
			if (storeStatus) setPipelineStarted(pipeline);
		}
		return status;
	}
	
	public PipelineOperationStatus stopPipeline()
	{
		List<InvocableSEPAElement> graphs = TemporaryGraphStorage.graphStorage.get(pipeline.getPipelineId());
		PipelineOperationStatus status = new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(), graphs).detachGraphs();
		
		if (status.isSuccess())
		{
			if (visualize) StorageManager.INSTANCE.getPipelineStorageAPI().deleteVisualization(pipeline.getPipelineId());
			if (storeStatus) setPipelineStopped(pipeline);
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
	
	public static void main(String[] args)
	{
		String testUrl = "http://192.168.84.38:8091/table/fc207c20-be7b-4ef9-aa60-7a35e1f9e2d8-FZI.SEPA.AOGhRDPWTbrcGHZNnvec";
		testUrl = testUrl.replaceFirst("[a-zA-Z]{4}://[a-zA-Z0-9\\-\\.]+:\\d+", "https://proasense.nissatech.com/actions");
		System.out.println(testUrl);
	}
}
