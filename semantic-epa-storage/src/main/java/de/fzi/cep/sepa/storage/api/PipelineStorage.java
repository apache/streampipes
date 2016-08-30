package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.RunningVisualization;
import de.fzi.cep.sepa.model.client.VirtualSensor;

public interface PipelineStorage {

	List<Pipeline> getAllPipelines();

	List<Pipeline> getAllUserPipelines();

	List<RunningVisualization> getRunningVisualizations();

	void storeVisualization(RunningVisualization vizualization);

	void deleteVisualization(String pipelineId);

	void storePipeline(Pipeline pipeline);

	void updatePipeline(Pipeline pipeline);

	Pipeline getPipeline(String pipelineId);

	void deletePipeline(String pipelineId);

	void store(Pipeline object);

	void storeVirtualSensor(String username, VirtualSensor virtualSensor);

	List<VirtualSensor> getVirtualSensors(String username);
}
