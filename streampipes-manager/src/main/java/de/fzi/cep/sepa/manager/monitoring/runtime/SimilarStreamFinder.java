package de.fzi.cep.sepa.manager.monitoring.runtime;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.quality.MeasurementCapability;
import de.fzi.cep.sepa.model.impl.quality.MeasurementObject;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class SimilarStreamFinder {

	private Pipeline pipeline;
	
	private List<EventStream> similarStreams;
	
	public SimilarStreamFinder(String pipelineId) {
		this.pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline(pipelineId);
		this.similarStreams = new ArrayList<>();
	}
	
	public boolean isReplacable() {
		if (pipeline.getStreams().size() > 1 || pipeline.getStreams().size() == 0) return false;
		else {
			return isSimilarStreamAvailable();
		}
	}

	private boolean isSimilarStreamAvailable() {
		
		List<SepDescription> seps = StorageManager.INSTANCE.getStorageAPI().getAllSEPs();
		List<EventStream> streams = getEventStreams(seps);
		
		EventStream pipelineInputStream = getStream();
		List<MeasurementCapability> pipelineInputStreamCapabilities = pipelineInputStream.getMeasurementCapability();
		List<MeasurementObject> pipelineInputStreamMeasurementObject = pipelineInputStream.getMeasurementObject();
		
		for(EventStream stream : streams) {
			if (!stream.getElementId().equals(pipelineInputStream.getElementId())) {
				if (matchesStream(pipelineInputStreamCapabilities, pipelineInputStreamMeasurementObject, stream.getMeasurementCapability(), stream.getMeasurementObject())) {
					similarStreams.add(stream);
				}
			}
		}
		
		return similarStreams.size() > 0;
	}
	
	private boolean matchesStream(
			List<MeasurementCapability> pipelineInputStreamCapabilities,
			List<MeasurementObject> pipelineInputStreamMeasurementObject,
			List<MeasurementCapability> measurementCapability,
			List<MeasurementObject> measurementObject) {
		return matchesCapability(pipelineInputStreamCapabilities, measurementCapability) &&
				matchesObject(pipelineInputStreamMeasurementObject, measurementObject);
	}

	private boolean matchesObject(
			List<MeasurementObject> pipelineInputStreamMeasurementObject,
			List<MeasurementObject> measurementObject) {
		if (pipelineInputStreamMeasurementObject == null | measurementObject == null) return false;
		else return pipelineInputStreamMeasurementObject.stream().allMatch(p -> measurementObject.stream().anyMatch(mc -> mc.getMeasuresObject().toString().equals(p.getMeasuresObject().toString())));
	}

	private boolean matchesCapability(
			List<MeasurementCapability> pipelineInputStreamCapabilities,
			List<MeasurementCapability> measurementCapability) {
		if (pipelineInputStreamCapabilities == null || measurementCapability == null) return false;
		else return pipelineInputStreamCapabilities.stream().allMatch(p -> measurementCapability.stream().anyMatch(mc -> mc.getCapability().toString().equals(p.getCapability().toString())));
	}

	private EventStream getStream() {
		String streamId = pipeline.getStreams().get(0).getElementId();
		
		return StorageManager.INSTANCE.getStorageAPI().getEventStreamById(streamId);
	}
	
	private List<EventStream> getEventStreams(List<SepDescription> seps) {
		List<EventStream> result = new ArrayList<>();
		for(SepDescription sep : seps) {
			result.addAll(sep.getEventStreams());
		}
		return result;
	}

	public List<EventStream> getSimilarStreams() {
		return similarStreams;
	}
	
	
}
