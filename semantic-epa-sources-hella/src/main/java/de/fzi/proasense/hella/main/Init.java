package de.fzi.proasense.hella.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.desc.EmbeddedModelSubmitter;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;

import de.fzi.proasense.hella.sources.MontracProducer;

public class Init extends EmbeddedModelSubmitter {

	@Override
	protected List<SemanticEventProcessingAgentDeclarer> epaDeclarers() {
		return new ArrayList<>();
	}

	@Override
	protected List<SemanticEventProducerDeclarer> sourceDeclarers() {
		return Arrays.asList(new MontracProducer());
	}

	@Override
	protected List<SemanticEventConsumerDeclarer> consumerDeclarers() {
		return new ArrayList<>();
	}

	@Override
	protected int port() {
		return 8080;
	}

	@Override
	protected String contextPath() {
		return "/sources-hella";
	}

	
}
