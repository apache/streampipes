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
