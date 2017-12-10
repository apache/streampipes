package org.streampipes.manager.monitoring.runtime;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.streampipes.manager.operations.Operations;
import org.streampipes.model.client.pipeline.Pipeline;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.storage.controller.StorageManager;

public class PipelineStreamReplacer {

	private String pipelineId;
	private SpDataStream streamToReplace;
	
	public PipelineStreamReplacer(String pipelineId, SpDataStream streamToReplace) {
		this.pipelineId = pipelineId;
		this.streamToReplace = streamToReplace;
	}
	
	public boolean replaceStream() {
		Pipeline currentPipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline(pipelineId);
		String streamDomId = currentPipeline.getStreams().get(0).getDOM();
		Operations.stopPipeline(currentPipeline);
		currentPipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline(pipelineId);
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
			StorageManager.INSTANCE.getPipelineStorageAPI().storePipeline(currentPipeline);
			Operations.startPipeline(StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline(newPipelineId));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Operations.startPipeline(StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline(pipelineId));
			return false;
		}
		
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
			for(SpDataStream stream : sep.getSpDataStreams()) {
				if (stream.getElementId().equals(streamToReplace2.getElementId())) return sep;
			}
		}
		
		throw new Exception("Stream not found");
	}
}
