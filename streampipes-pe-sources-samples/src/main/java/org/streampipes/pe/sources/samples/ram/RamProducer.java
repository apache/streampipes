package org.streampipes.pe.sources.samples.ram;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.impl.graph.SepDescription;

public class RamProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("source_ram", "Ram", "Ram");
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> eventStreams = new ArrayList<EventStreamDeclarer>();
		
		eventStreams.add(new RamPositionSetPoint());
		eventStreams.add(new RamPositionMeasuredValue());
		eventStreams.add(new RamVelocitySetPoint());
		eventStreams.add(new RamVelocityMeasuredValue());
		return eventStreams;
	}

}
