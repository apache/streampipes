package de.fzi.cep.sepa.sources.samples.twitter;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;

public class TwitterStreamProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("source/twitter", "Twitter", "Twitter Event Producer");
		sep.setIconUrl(SourcesConfig.iconBaseUrl + "/Twitter_Icon" +"_HQ.png");
		
		return sep;
	}

	
	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
		
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
