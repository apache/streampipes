package de.fzi.cep.sepa.sources.samples.random;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventSource;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.samples.util.Utils;

public class RandomDataProducer implements SemanticEventProducerDeclarer {
	
	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("/random", "Random", "Random Event Producer", "", Utils.createDomain(Domain.DOMAIN_PERSONAL_ASSISTANT), new EventSource());		
		return sep;
	}

	
	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		
		List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
		
		try {
			//streams.add(new RandomTextStream());
			streams.add(new RandomNumberStream());
			streams.add(new NestedListRandomNumberStream());
			streams.add(new NestedRandomNumberStream());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	return streams;
	}

}
