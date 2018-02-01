package org.streampipes.pe.processors.esper.aggregate.count;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.ArrayList;
import java.util.List;

public class CountController extends StandaloneEventProcessorDeclarerSingleton<CountParameter> {

	@Override
	public DataProcessorDescription declareModel() {

		return ProcessingElementBuilder.create("count", "Count Aggregation",
						"Performs an aggregation based on a given event property and outputs the number of occurrences.")
						.category(DataProcessorType.AGGREGATE)
						.iconUrl(EsperConfig.iconBaseUrl +"/Counter_Icon_HQ.png")
//						.requiredPropertyStream1(EpRequirements.anyProperty())
                		.setStream1()
						.naryMappingPropertyWithoutRequirement("groupBy", "Group Stream By", "")
						.outputStrategy(OutputStrategies.append(EpProperties.integerEp(Labels.empty(), "countValue",
										"http://schema.org/Number")))
						.requiredIntegerParameter("timeWindow", "Time Window Size", "Size of the time window " +
										"in seconds")
						.requiredSingleValueSelection("scale", "Time Window Scale", "",
										Options.from("Hours", "Minutes", "Seconds"))
						.supportedFormats(StandardTransportFormat.standardFormat())
						.supportedProtocols(StandardTransportFormat.standardProtocols())
						.build();
	}

	@Override
	public ConfiguredEventProcessor<CountParameter> onInvocation(DataProcessorInvocation sepa, ProcessingElementParameterExtractor extractor) {
		List<String> groupBy = SepaUtils.getMultipleMappingPropertyNames(sepa,
						"groupBy", true);

		int timeWindowSize = Integer.parseInt(((FreeTextStaticProperty) (SepaUtils
						.getStaticPropertyByInternalName(sepa, "timeWindow"))).getValue());

		String scale = SepaUtils.getOneOfProperty(sepa,
						"scale");

		TimeScale timeScale;

		if (scale.equals("Hours")) timeScale = TimeScale.HOURS;
		else if (scale.equals("Minutes")) timeScale = TimeScale.MINUTES;
		else timeScale = TimeScale.SECONDS;

		List<String> selectProperties = new ArrayList<>();
		for(EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
			selectProperties.add(p.getRuntimeName());
		}

		CountParameter staticParam = new CountParameter(sepa, timeWindowSize, groupBy, timeScale, selectProperties);

		return new ConfiguredEventProcessor<>(staticParam, Count::new);
	}
}
