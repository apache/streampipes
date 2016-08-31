package de.fzi.cep.sepa.manager.monitoring.runtime;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.Pipeline;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class PipelineStreamReplacer {

	private String pipelineId;
	private EventStream streamToReplace;
	
	public PipelineStreamReplacer(String pipelineId, EventStream streamToReplace) {
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
			
			for(SepaInvocation sepaClient : currentPipeline.getSepas()) {
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

	private SepDescription getSep(EventStream streamToReplace2) throws Exception {
		List<SepDescription> seps = StorageManager.INSTANCE.getStorageAPI().getAllSEPs();
		
		for(SepDescription sep : seps) {
			for(EventStream stream : sep.getEventStreams()) {
				if (stream.getElementId().equals(streamToReplace2.getElementId())) return sep;
			}
		}
		
		throw new Exception("Stream not found");
	}
}
