package de.fzi.cep.sepa.sources.samples.taxi;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;

public class NYCTaxiProducer implements SemanticEventProducerDeclarer{

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("source/taxi", "NYC Taxi Data", "NYC Taxi Data Producer");
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
