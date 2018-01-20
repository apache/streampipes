package org.streampipes.pe.sources.mhwirth.ram;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;

public class RamProducer implements SemanticEventProducerDeclarer {

	@Override
	public DataSourceDescription declareModel() {
		DataSourceDescription sep = new DataSourceDescription("source-ram", "Ram", "Ram");
		return sep;
	}

	@Override
	public List<DataStreamDeclarer> getEventStreams() {
		List<DataStreamDeclarer> eventStreams = new ArrayList<DataStreamDeclarer>();
		
		eventStreams.add(new RamPositionSetPoint());
		eventStreams.add(new RamPositionMeasuredValue());
		eventStreams.add(new RamVelocitySetPoint());
		eventStreams.add(new RamVelocityMeasuredValue());
		return eventStreams;
	}

}
