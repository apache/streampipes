package ${package}.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.desc.EmbeddedModelSubmitter;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;

import ${package}.sources.${sourceName}Producer;

public class Init extends EmbeddedModelSubmitter {

	@Override
	protected List<SemanticEventProcessingAgentDeclarer> epaDeclarers() {
		return new ArrayList<>();
	}

	@Override
	protected List<SemanticEventProducerDeclarer> sourceDeclarers() {
		return Arrays.asList(new ${sourceName}Producer());
	}

	@Override
	protected List<SemanticEventConsumerDeclarer> consumerDeclarers() {
		return new ArrayList<>();
	}

	@Override
	protected int port() {
		return ${port};
	}

	@Override
	protected String contextPath() {
		return "/${contextPath}";
	}

	
}
