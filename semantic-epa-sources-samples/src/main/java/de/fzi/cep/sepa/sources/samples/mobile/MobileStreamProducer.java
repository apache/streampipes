package de.fzi.cep.sepa.sources.samples.mobile;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventSource;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;
import de.fzi.cep.sepa.sources.samples.util.Utils;

public class MobileStreamProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("/mobile", "Mobile phone events", "Mobile phone event producer", "", Utils.createDomain(Domain.DOMAIN_PERSONAL_ASSISTANT), new EventSource());
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
