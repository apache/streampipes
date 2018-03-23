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

package org.streampipes.manager.monitoring.runtime;

import org.streampipes.manager.operations.Operations;
import org.streampipes.model.SpDataSequence;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.storage.api.IPipelineStorage;
import org.streampipes.storage.management.StorageDispatcher;
import org.streampipes.storage.management.StorageManager;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PipelineStreamReplacer {

	private String pipelineId;
	private SpDataSequence streamToReplace;
	
	public PipelineStreamReplacer(String pipelineId, SpDataSequence streamToReplace) {
		this.pipelineId = pipelineId;
		this.streamToReplace = streamToReplace;
	}
	
	public boolean replaceStream() {
		Pipeline currentPipeline = getPipelineStorage().getPipeline(pipelineId);
		String streamDomId = currentPipeline.getStreams().get(0).getDOM();
		Operations.stopPipeline(currentPipeline);
		currentPipeline = getPipelineStorage().getPipeline(pipelineId);
		try {
			streamToReplace.setDOM(streamDomId);
			currentPipeline.setStreams(Arrays.asList(streamToReplace));
			
			for(DataProcessorInvocation sepaClient : currentPipeline.getSepas()) {
				// TODO
//				for(StaticProperty staticProperty : sepaClient.getStaticProperties()) {
//					if (staticProperty.getType() == StaticPropertyType.CUSTOM_OUTPUT) {
//						CheckboxInput input = (CheckboxInput) staticProperty.getInput();
//						for(Option option : input.getOptions()) {
//							option.setElementId(getElementId(option.getHumanDescription()));
//						}
//					}
//					else if (staticProperty.getType() == StaticPropertyType.MAPPING_PROPERTY) {
//						SelectFormInput input = (SelectFormInput) staticProperty.getInput();
//						for(Option option : input.getOptions()) {
//							option.setElementId(getElementId(option.getHumanDescription()));
//						}
//					}
//				}
			}
			String newPipelineId = UUID.randomUUID().toString();
			currentPipeline.setPipelineId(newPipelineId);
			currentPipeline.setRev(null);
			currentPipeline.setName(currentPipeline.getName() +" (Replacement)");
			getPipelineStorage().storePipeline(currentPipeline);
			Operations.startPipeline(getPipelineStorage().getPipeline(newPipelineId));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Operations.startPipeline(getPipelineStorage().getPipeline(pipelineId));
			return false;
		}
		
	}

	private IPipelineStorage getPipelineStorage() {
		return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();
	}

	private String getElementId(String humanDescription) throws Exception {
		for(EventProperty p : streamToReplace.getEventSchema().getEventProperties()) {
			if (p.getRuntimeName().equals(humanDescription)) return p.getElementId();
		}
		
		throw new Exception("Property not found");
	}

	private DataSourceDescription getSep(SpDataStream streamToReplace2) throws Exception {
		List<DataSourceDescription> seps = StorageManager.INSTANCE.getStorageAPI().getAllSEPs();
		
		for(DataSourceDescription sep : seps) {
			for(SpDataSequence stream : sep.getSpDataStreams()) {
				if (stream.getElementId().equals(streamToReplace2.getElementId())) return sep;
			}
		}
		
		throw new Exception("Stream not found");
	}
}
