/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.execution.http;

import org.lightcouch.DocumentConflictException;
import org.streampipes.manager.execution.status.PipelineStatusManager;
import org.streampipes.manager.execution.status.SepMonitoringManager;
import org.streampipes.manager.util.TemporaryGraphStorage;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.client.pipeline.PipelineOperationStatus;
import org.streampipes.model.client.pipeline.PipelineStatusMessage;
import org.streampipes.model.client.pipeline.PipelineStatusMessageType;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.storage.api.IPipelineStorage;
import org.streampipes.storage.management.StorageDispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

		List<DataProcessorInvocation> sepas = pipeline.getSepas();
		List<DataSinkInvocation> secs = pipeline.getActions();
		List<SpDataSet> dataSets = pipeline.getStreams().stream().filter(s -> s instanceof SpDataSet).map(s -> new
						SpDataSet((SpDataSet) s)).collect(Collectors.toList());

		for (SpDataSet ds : dataSets) {
			ds.setCorrespondingPipeline(pipeline.getPipelineId());
		}

		List<InvocableStreamPipesEntity> graphs = new ArrayList<>();
		graphs.addAll(sepas);
		graphs.addAll(secs);

		graphs.forEach(g -> g.setStreamRequirements(Arrays.asList()));

		PipelineOperationStatus status = new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(), graphs, dataSets)
						.invokeGraphs();
		
		if (status.isSuccess()) 
		{
			storeInvocationGraphs(pipeline.getPipelineId(), graphs, dataSets);
			
			PipelineStatusManager.addPipelineStatus(pipeline.getPipelineId(), 
					new PipelineStatusMessage(pipeline.getPipelineId(), System.currentTimeMillis(), PipelineStatusMessageType.PIPELINE_STARTED.title(), PipelineStatusMessageType.PIPELINE_STARTED.description()));
			
			if (monitor) SepMonitoringManager.addObserver(pipeline.getPipelineId());
			
			if (storeStatus) setPipelineStarted(pipeline);
		}
		return status;
	}
	
	public PipelineOperationStatus stopPipeline()
	{
		List<InvocableStreamPipesEntity> graphs = TemporaryGraphStorage.graphStorage.get(pipeline.getPipelineId());
		List<SpDataSet> dataSets = TemporaryGraphStorage.datasetStorage.get(pipeline.getPipelineId());

		PipelineOperationStatus status = new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(), graphs, dataSets)
						.detachGraphs();
		
		if (status.isSuccess())
		{
			if (visualize) StorageDispatcher.INSTANCE.getNoSqlStore().getVisualizationStorageApi().deleteVisualization(pipeline
						.getPipelineId());
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
			getPipelineStorageApi().updatePipeline(pipeline);
		} catch (DocumentConflictException dce)
		{
			//dce.printStackTrace();
		}
	}
	
	private void setPipelineStopped(Pipeline pipeline) {
		pipeline.setRunning(false);
		getPipelineStorageApi().updatePipeline(pipeline);
	}
	
	private void storeInvocationGraphs(String pipelineId, List<InvocableStreamPipesEntity> graphs, List<SpDataSet> dataSets)
	{
		TemporaryGraphStorage.graphStorage.put(pipelineId, graphs);
		TemporaryGraphStorage.datasetStorage.put(pipelineId, dataSets);
	}

	private IPipelineStorage getPipelineStorageApi() {
		return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();
	}

}
