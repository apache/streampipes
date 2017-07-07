package org.streampipes.pe.sources.samples.mobile;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

public class MobileStreamProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("source/mobile", "Mobile phone events", "Mobile phone event producer");
		sep.setIconUrl(SourcesConfig.iconBaseUrl + "/Mobile_Phone" +"_HQ.png");
		
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
			
		streams.add(new MobileLocationStream());	
			
		return streams;
	}

}
