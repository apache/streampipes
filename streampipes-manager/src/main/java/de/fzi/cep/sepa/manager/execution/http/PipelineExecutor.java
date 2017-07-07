package de.fzi.cep.sepa.manager.execution.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import org.lightcouch.DocumentConflictException;

import de.fzi.cep.sepa.manager.execution.status.PipelineStatusManager;
import de.fzi.cep.sepa.manager.execution.status.SepMonitoringManager;
import de.fzi.cep.sepa.manager.util.TemporaryGraphStorage;
import de.fzi.cep.sepa.model.client.pipeline.PipelineOperationStatus;
import de.fzi.cep.sepa.model.client.pipeline.PipelineStatusMessage;
import de.fzi.cep.sepa.model.client.pipeline.PipelineStatusMessageType;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class PipelineExecutor {

	private Pipeline pipeline;
	private boolean visualize;
	private boolean storeStatus;
	private boolean monitor;
	
	public PipelineExecutor(Pipeline pipeline, boolean visualize, boolean storeStatus, boolean monitor)
	{
		this.pipeline = pipeline;
		this.visualize = visualize;
		this.storeStatus = storeStatus;
		this.monitor = monitor;
	}
	
	public PipelineOperationStatus startPipeline()
	{

		List<SepaInvocation> sepas = pipeline.getSepas();
		List<SecInvocation> secs = pipeline.getActions();

		List<InvocableSEPAElement> graphs = new ArrayList<>();
		graphs.addAll(sepas);
		graphs.addAll(secs);

		graphs.forEach(g -> g.setStreamRequirements(Arrays.asList()));

		PipelineOperationStatus status = new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(), graphs).invokeGraphs();
		
		if (status.isSuccess()) 
		{
			storeInvocationGraphs(pipeline.getPipelineId(), graphs);
			
			PipelineStatusManager.addPipelineStatus(pipeline.getPipelineId(), 
					new PipelineStatusMessage(pipeline.getPipelineId(), System.currentTimeMillis(), PipelineStatusMessageType.PIPELINE_STARTED.title(), PipelineStatusMessageType.PIPELINE_STARTED.description()));
			
			if (monitor) SepMonitoringManager.addObserver(pipeline.getPipelineId());
			
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
			if (visualize) StorageManager.INSTANCE.getVisualizationStorageApi().deleteVisualization(pipeline.getPipelineId());
			if (storeStatus) setPipelineStopped(pipeline);
			
			PipelineStatusManager.addPipelineStatus(pipeline.getPipelineId(), 
					new PipelineStatusMessage(pipeline.getPipelineId(), System.currentTimeMillis(), PipelineStatusMessageType.PIPELINE_STOPPED.title(), PipelineStatusMessageType.PIPELINE_STOPPED.description()));
			
			if (monitor) SepMonitoringManager.removeObserver(pipeline.getPipelineId());
			
		}
		return status;
	}
	
	private void setPipelineStarted(Pipeline pipeline) {
		pipeline.setRunning(true);
		pipeline.setStartedAt(new Date().getTime());
		try {
			StorageManager.INSTANCE.getPipelineStorageAPI().updatePipeline(pipeline);
		} catch (DocumentConflictException dce)
		{
			//dce.printStackTrace();
		}
	}
	
	private void setPipelineStopped(Pipeline pipeline) {
		pipeline.setRunning(false);
		StorageManager.INSTANCE.getPipelineStorageAPI().updatePipeline(pipeline);
	}
	
	private void storeInvocationGraphs(String pipelineId, List<InvocableSEPAElement> graphs)
	{
		TemporaryGraphStorage.graphStorage.put(pipelineId, graphs);
	}

}
