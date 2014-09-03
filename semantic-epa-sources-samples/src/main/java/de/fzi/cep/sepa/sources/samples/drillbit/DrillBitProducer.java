package de.fzi.cep.sepa.sources.samples.drillbit;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventSource;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.sources.samples.util.Utils;

public class DrillBitProducer implements SemanticEventProducerDeclarer {


	@Override
	public SEP declareModel() {
		SEP sep = new SEP("/drillBit", "Drill Bit", "Drill Bit", "", Utils.createDomain(Domain.DOMAIN_PROASENSE), new EventSource());
		//sep.setIconUrl(SourcesConfig.iconBaseUrl + "/DDM_Icon" +"_HQ.png");
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> eventStreams = new ArrayList<EventStreamDeclarer>();
		
		eventStreams.add(new WeightOnBit());
		return eventStreams;
	}
}
