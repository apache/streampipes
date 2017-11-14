package org.streampipes.pe.processors.esper.aggregate.rate;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.wrapper.ConfiguredEventProcessor;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

public class EventRateController extends StandaloneEventProcessorDeclarerSingleton<EventRateParameter> {

	@Override
	public DataProcessorDescription declareModel() {

		return ProcessingElementBuilder.create("eventrate", "Event rate", "Computes current event rate")
						.category(DataProcessorType.AGGREGATE)
						.iconUrl(EsperConfig.getIconUrl("rate-icon"))
						.requiredPropertyStream1(EpRequirements.anyProperty())
						.outputStrategy(OutputStrategies.fixed(EpProperties.doubleEp(Labels.empty(), "rate",
										"http://schema.org/Number")))
						.requiredIntegerParameter("rate", "Average/Sec", "" +
										"in seconds")
						.requiredIntegerParameter("output", "Output Every (seconds)", "")
						.supportedFormats(StandardTransportFormat.standardFormat())
						.supportedProtocols(StandardTransportFormat.standardProtocols())
						.build();
	}

	@Override
	public ConfiguredEventProcessor<EventRateParameter, EventProcessor<EventRateParameter>> onInvocation(DataProcessorInvocation
																																																								 sepa) {
		ProcessingElementParameterExtractor extractor = getExtractor(sepa);

		Integer avgRate = extractor.singleValueParameter("rate", Integer.class);
		Integer outputRate = extractor.singleValueParameter("output", Integer.class);

		String topicPrefix = "topic://";
		EventRateParameter staticParam = new EventRateParameter(sepa, avgRate, outputRate
						, topicPrefix + sepa.getOutputStream().getEventGrounding().getTransportProtocol().getTopicName());

		return new ConfiguredEventProcessor<>(staticParam, EventRate::new);
	}
}