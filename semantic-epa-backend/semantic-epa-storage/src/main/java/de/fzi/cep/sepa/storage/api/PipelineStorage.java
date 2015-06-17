package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.RunningVisualization;
import de.fzi.cep.sepa.model.client.VirtualSensor;

public interface PipelineStorage {

	public List<Pipeline> getAllPipelines();
	
	public List<RunningVisualization> getRunningVisualizations();
	
	public void storeVisualization(RunningVisualization vizualization);
	
	public void deleteVisualization(String pipelineId);
	
	public void storePipeline(Pipeline pipeline);
	
	public void updatePipeline(Pipeline pipeline);
	
	public Pipeline getPipeline(String pipelineId);
	
	public void deletePipeline(String pipelineId);
	
	public <T> void store(T object);
	
	public void storeVirtualSensor(VirtualSensor virtualSensor);
	
	public List<VirtualSensor> getVirtualSensors();
}
