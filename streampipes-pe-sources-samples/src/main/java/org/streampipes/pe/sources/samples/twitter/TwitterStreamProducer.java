package org.streampipes.pe.sources.samples.twitter;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

public class TwitterStreamProducer implements SemanticEventProducerDeclarer {

	@Override
	public DataSourceDescription declareModel() {
		DataSourceDescription sep = new DataSourceDescription("source/twitter", "Twitter", "Twitter Event Producer");
		sep.setIconUrl(SourcesConfig.iconBaseUrl + "/Twitter_Icon" +"_HQ.png");
		
		return sep;
	}

	
	@Override
	public List<DataStreamDeclarer> getEventStreams() {
		List<DataStreamDeclarer> streams = new ArrayList<DataStreamDeclarer>();
		
		try {
			streams.add(new TwitterSampleStream());
			streams.add(new TwitterGeoStream());
			//streams.add(new TweetsGermanyStream());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	return streams;
	}
}
