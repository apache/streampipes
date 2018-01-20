package org.streampipes.pe.sources.samples.taxi;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

import java.util.ArrayList;
import java.util.List;

public class NYCTaxiProducer implements SemanticEventProducerDeclarer{

	@Override
	public DataSourceDescription declareModel() {
		DataSourceDescription sep = new DataSourceDescription("source-taxi", "NYC Taxi Data", "NYC Taxi Data " +
						"Producer");
		sep.setIconUrl(SourcesConfig.iconBaseUrl + "/Taxi_Icon_1" +"_HQ.png");
		return sep;
	}

	@Override
	public List<DataStreamDeclarer> getEventStreams() {
		List<DataStreamDeclarer> streams = new ArrayList<DataStreamDeclarer>();
		streams.add(new NYCTaxiStream());
		streams.add(new NycTest01Stream());
		streams.add(new NycTest02Stream());
		streams.add(new NycTest03Stream());
		
		return streams;
	}
}
