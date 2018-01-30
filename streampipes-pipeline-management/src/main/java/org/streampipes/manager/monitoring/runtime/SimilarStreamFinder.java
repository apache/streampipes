package org.streampipes.manager.monitoring.runtime;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.quality.MeasurementCapability;
import org.streampipes.model.quality.MeasurementObject;
import org.streampipes.storage.management.StorageDispatcher;
import org.streampipes.storage.management.StorageManager;

import java.util.ArrayList;
import java.util.List;

public class SimilarStreamFinder {

	private Pipeline pipeline;
	
	private List<SpDataStream> similarStreams;
	
	public SimilarStreamFinder(String pipelineId) {
		this.pipeline = StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().getPipeline(pipelineId);
		this.similarStreams = new ArrayList<>();
	}
	
	public boolean isReplacable() {
		if (pipeline.getStreams().size() > 1 || pipeline.getStreams().size() == 0) return false;
		else {
			return isSimilarStreamAvailable();
		}
	}

	private boolean isSimilarStreamAvailable() {
		
		List<DataSourceDescription> seps = StorageManager.INSTANCE.getStorageAPI().getAllSEPs();
		List<SpDataStream> streams = getEventStreams(seps);
		
		SpDataStream pipelineInputStream = getStream();
		List<MeasurementCapability> pipelineInputStreamCapabilities = pipelineInputStream.getMeasurementCapability();
		List<MeasurementObject> pipelineInputStreamMeasurementObject = pipelineInputStream.getMeasurementObject();
		
		for(SpDataStream stream : streams) {
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

	private SpDataStream getStream() {
		String streamId = pipeline.getStreams().get(0).getElementId();
		
		return StorageManager.INSTANCE.getStorageAPI().getEventStreamById(streamId);
	}
	
	private List<SpDataStream> getEventStreams(List<DataSourceDescription> seps) {
		List<SpDataStream> result = new ArrayList<>();
		for(DataSourceDescription sep : seps) {
			result.addAll(sep.getSpDataStreams());
		}
		return result;
	}

	public List<SpDataStream> getSimilarStreams() {
		return similarStreams;
	}
	
	
}
