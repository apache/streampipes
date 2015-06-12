package de.fzi.cep.sepa.sources.samples.twitter;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventSource;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;
import de.fzi.cep.sepa.sources.samples.util.Utils;

public class TwitterStreamProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("/twitter", "Twitter", "Twitter Event Producer", "", Utils.createDomain(Domain.DOMAIN_PERSONAL_ASSISTANT, Domain.DOMAIN_PROASENSE), new EventSource());
		sep.setIconUrl(SourcesConfig.iconBaseUrl + "/Twitter_Icon" +"_HQ.png");
		
		return sep;
	}

	
	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
		
		try {
			streams.add(new TwitterSampleStream());
			streams.add(new TwitterGeoStream());
			streams.add(new TweetsGermanyStream());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	return streams;
	}
}
