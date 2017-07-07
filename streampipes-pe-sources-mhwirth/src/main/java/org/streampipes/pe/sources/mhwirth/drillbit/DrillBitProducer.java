package org.streampipes.pe.sources.mhwirth.drillbit;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.impl.graph.SepDescription;

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
