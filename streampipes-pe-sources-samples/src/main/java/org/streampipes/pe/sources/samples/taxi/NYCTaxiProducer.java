package org.streampipes.pe.sources.samples.taxi;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

import java.util.ArrayList;
import java.util.List;

public class NYCTaxiProducer implements SemanticEventProducerDeclarer{

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("source-taxi", "NYC Taxi Data", "NYC Taxi Data " +
						"Producer");
		sep.setIconUrl(SourcesConfig.iconBaseUrl + "/Taxi_Icon_1" +"_HQ.png");
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
		streams.add(new NYCTaxiStream());
		streams.add(new NycTest01Stream());
		streams.add(new NycTest02Stream());
		streams.add(new NycTest03Stream());
		
		return streams;
	}
}
