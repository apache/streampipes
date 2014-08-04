package de.fzi.cep.sepa.sources.samples.twitter;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventSource;
import de.fzi.cep.sepa.model.impl.SEP;

public class TwitterStreamProducer implements SemanticEventProducerDeclarer {

	@Override
	public SEP declareSemanticEventProducer() {
		SEP sep = new SEP("http://fzi.de/", "Twitter", "Twitter Event Producer", createDomain(Domain.DOMAIN_PERSONAL_ASSISTANT), new EventSource());
		
		return sep;
	}

	@Override
	public int declarePort() {
		return 8089;
	}

	@Override
	public String declareURIPath() {
		return "twitter";
	}
	
	private List<Domain> createDomain(Domain...domains)
	{
		ArrayList<Domain> domainList = new ArrayList<Domain>();
		for(Domain d : domains)
			domainList.add(d);
			
		return domainList;
	}

}
