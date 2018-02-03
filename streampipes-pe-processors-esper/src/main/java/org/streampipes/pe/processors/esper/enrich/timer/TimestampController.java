package org.streampipes.pe.processors.esper.enrich.timer;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.ArrayList;
import java.util.List;

public class TimestampController extends StandaloneEventProcessorDeclarerSingleton<TimestampParameter> {

	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder.create("enrich_timestamp", "Timestamp Enrichment", "Appends the current time in ms to the event payload")
						.iconUrl(EsperConfig.getIconUrl("Timer_Icon_HQ"))
						.category(DataProcessorType.ENRICH)
						.requiredPropertyStream1(EpRequirements.anyProperty())
						.outputStrategy(OutputStrategies.append(EpProperties.longEp(Labels.empty(), "appendedTime", SO.DateTime)))
						.supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
						.supportedFormats(SupportedFormats.jsonFormat())
						.build();
	}

	@Override
	public ConfiguredEventProcessor<TimestampParameter> onInvocation(DataProcessorInvocation
                                                                           sepa, ProcessingElementParameterExtractor extractor) {
		AppendOutputStrategy strategy = (AppendOutputStrategy) sepa.getOutputStrategies().get(0);

		String appendTimePropertyName = SepaUtils.getEventPropertyName(strategy.getEventProperties(), "appendedTime");

		List<String> selectProperties = new ArrayList<>();
		for(EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
			selectProperties.add(p.getRuntimeName());
		}

		TimestampParameter staticParam = new TimestampParameter (
						sepa,
						appendTimePropertyName,
						selectProperties);

		return new ConfiguredEventProcessor<>(staticParam, TimestampEnrichment::new);
	}
}
