package org.streampipes.pe.processors.esper.aggregate.rate;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.wrapper.esper.config.EsperConfig;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarer;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;

public class EventRateController extends StandaloneEventProcessorDeclarer<EventRateParameter> {

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

		return submit(staticParam, EventRate::new);

	}
}