package de.fzi.proasense.monitoring.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.desc.EmbeddedModelSubmitter;
import de.fzi.cep.sepa.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventProducerDeclarer;
import de.fzi.proasense.monitoring.sources.MonitoringProducer;

public class Init extends EmbeddedModelSubmitter {

	@Override
	protected List<SemanticEventProcessingAgentDeclarer> epaDeclarers() {
		return new ArrayList<>();
	}

	@Override
	protected List<SemanticEventProducerDeclarer> sourceDeclarers() {
		return Arrays.asList(new MonitoringProducer());
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
		return "/sources-monitoring";
	}

	
}
