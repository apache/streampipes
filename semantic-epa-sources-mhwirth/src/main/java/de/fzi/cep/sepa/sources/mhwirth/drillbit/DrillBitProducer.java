package de.fzi.cep.sepa.sources.mhwirth.drillbit;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

public class DrillBitProducer implements SemanticEventProducerDeclarer {


	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("source-drillBit", "Drill Bit", "Drill Bit");
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
