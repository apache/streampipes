package de.fzi.cep.sepa.component.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.actions.samples.areachart.AreaChartController;
import de.fzi.cep.sepa.actions.samples.maparea.MapAreaController;
import de.fzi.cep.sepa.component.main.algorithm.langdetect.LanguageDetectionController;
import de.fzi.cep.sepa.desc.EmbeddedModelSubmitter;
import de.fzi.cep.sepa.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventProducerDeclarer;

public class Init extends EmbeddedModelSubmitter {

	@Override
	protected List<SemanticEventProcessingAgentDeclarer> epaDeclarers() {
		return Arrays.asList(new LanguageDetectionController());
	}

	@Override
	protected List<SemanticEventProducerDeclarer> sourceDeclarers() {
		return new ArrayList<>();
	}

	@Override
	protected List<SemanticEventConsumerDeclarer> consumerDeclarers() {
		return Arrays.asList(new AreaChartController(), new MapAreaController());
	}

	@Override
	protected int port() {
		return 8080;
	}

	@Override
	protected String contextPath() {
		return "/semantic-epa-samples-embedded";
	}

	
}
