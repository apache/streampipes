package org.semantic.epa.sources.mhwirth.sources;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

import org.semantic.epa.sources.mhwirth.streams.DrillingRpmStream;

public class DDMProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		
		SepDescription sep = new SepDescription("source/{sourceName.toLowerCase()}", "", "");
		
		return sep;
	}

	
	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		
		List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
		
		streams.add(new DrillingRpmStream());
		
		return streams;
	}
}
