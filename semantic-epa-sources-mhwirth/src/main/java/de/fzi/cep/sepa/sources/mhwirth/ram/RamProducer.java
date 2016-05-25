package de.fzi.cep.sepa.sources.mhwirth.ram;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

public class RamProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("source-ram", "Ram", "Ram");
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
