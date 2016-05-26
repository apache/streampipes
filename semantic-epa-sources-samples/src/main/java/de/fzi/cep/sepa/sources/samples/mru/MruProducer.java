package de.fzi.cep.sepa.sources.samples.mru;

import java.util.List;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

public class MruProducer implements SemanticEventProducerDeclarer{

	@Override
	public SepDescription declareModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		// TODO Auto-generated method stub
		return null;
	}

}
