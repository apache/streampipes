package de.fzi.cep.sepa.sources.samples.taxi;

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

public class NYCTaxiProducer implements SemanticEventProducerDeclarer{

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("/taxi", "NYC Taxi Data", "NYC Taxi Data Producer", "", Utils.createDomain(Domain.DOMAIN_PERSONAL_ASSISTANT), new EventSource());
		sep.setIconUrl(SourcesConfig.iconBaseUrl + "/Taxi_Icon_1" +"_HQ.png");
		return sep;
	}

	
	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
		
		try {
			streams.add(new NYCTaxiStream());
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	return streams;
	}
}
