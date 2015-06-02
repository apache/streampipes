package de.fzi.cep.sepa.sources.samples.ram;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventSource;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.samples.drillbit.WeightOnBit;
import de.fzi.cep.sepa.sources.samples.util.Utils;

public class RamProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("/ram", "Ram", "Ram", "", Utils.createDomain(Domain.DOMAIN_PROASENSE), new EventSource());
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
