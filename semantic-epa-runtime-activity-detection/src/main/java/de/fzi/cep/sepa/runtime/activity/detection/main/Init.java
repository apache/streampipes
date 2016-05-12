package de.fzi.cep.sepa.runtime.activity.detection.main;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.EmbeddedModelSubmitter;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;

public class Init extends EmbeddedModelSubmitter {

	@Override
	protected List<SemanticEventProcessingAgentDeclarer> epaDeclarers() {
		List<SemanticEventProcessingAgentDeclarer> l = new ArrayList<SemanticEventProcessingAgentDeclarer>();
		l.add(new ActivityDetectionController());
		return l;
	}

	@Override
	protected List<SemanticEventProducerDeclarer> sourceDeclarers() {
		return new ArrayList<SemanticEventProducerDeclarer>();
	}

	@Override
	protected List<SemanticEventConsumerDeclarer> consumerDeclarers() {
		return new ArrayList<SemanticEventConsumerDeclarer>();
	}

	@Override
	protected int port() {
		return 8080;
	}

	@Override
	protected String contextPath() {
		return "/activity-detection";
	}

}
