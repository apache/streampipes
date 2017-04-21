package de.fzi.cep.sepa.esper.aggregate.rate;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.extractor.ProcessingElementParameterExtractor;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;

public class EventRateController extends FlatEpDeclarer<EventRateParameter> {

	@Override
	public SepaDescription declareModel() {

		return ProcessingElementBuilder.create("eventrate", "Event rate", "Computes current event rate")
						.category(EpaType.AGGREGATE)
						.iconUrl(EsperConfig.getIconUrl("rate-icon"))
						.requiredPropertyStream1(EpRequirements.anyProperty())
						.outputStrategy(OutputStrategies.fixed(EpProperties.doubleEp("rate",
										"http://schema.org/Number")))
						.requiredIntegerParameter("rate", "Average/Sec", "" +
										"in seconds")
						.requiredIntegerParameter("output", "Output Every (seconds)", "")
						.supportedFormats(StandardTransportFormat.standardFormat())
						.supportedProtocols(StandardTransportFormat.standardProtocols())
						.build();
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {

		ProcessingElementParameterExtractor extractor = getExtractor(sepa);
	
		Integer avgRate = extractor.singleValueParameter("rate", Integer.class);
		Integer outputRate = extractor.singleValueParameter("output", Integer.class);
	
		String topicPrefix = "topic://";
		EventRateParameter staticParam = new EventRateParameter(sepa, avgRate, outputRate
						, topicPrefix + sepa.getOutputStream().getEventGrounding().getTransportProtocol().getTopicName());

		return submit(staticParam, EventRate::new, sepa);

	}
}