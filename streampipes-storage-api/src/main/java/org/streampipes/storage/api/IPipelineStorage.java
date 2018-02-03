package org.streampipes.storage.api;

import org.streampipes.model.client.VirtualSensor;
import org.streampipes.model.client.pipeline.Pipeline;

import java.util.List;

public interface IPipelineStorage {

	List<Pipeline> getAllPipelines();

	List<Pipeline> getSystemPipelines();

	List<Pipeline> getAllUserPipelines();

	void storePipeline(Pipeline pipeline);

	void updatePipeline(Pipeline pipeline);

	Pipeline getPipeline(String pipelineId);

	void deletePipeline(String pipelineId);

	void store(Pipeline object);

	void storeVirtualSensor(String username, VirtualSensor virtualSensor);

	List<VirtualSensor> getVirtualSensors(String username);
}
