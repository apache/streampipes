package org.streampipes.pe.processors.esper.aggregate.avg;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.wrapper.esper.config.EsperConfig;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.wrapper.standalone.declarer.FlatEpDeclarer;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;

import java.util.ArrayList;
import java.util.List;

public class AggregationController extends FlatEpDeclarer<AggregationParameter> {

	@Override
	public SepaDescription declareModel() {

		return ProcessingElementBuilder.create("aggregation", "Aggregation", "Performs different " +
						"aggregation functions")
						.category(EpaType.AGGREGATE)
						.iconUrl(EsperConfig.iconBaseUrl +"/Aggregation_Icon_HQ.png")
						.stream1PropertyRequirementWithUnaryMapping(EpRequirements.numberReq(), "aggregate",
										"Property Selection", "Specifies the event property from your stream that should be aggregated.")
						.naryMappingPropertyWithoutRequirement("groupBy", "Group by", "Partitions the incoming stream by the selected event " +
						"properties")
						.outputStrategy(OutputStrategies.append(EpProperties.doubleEp("aggregatedValue",
										"http://schema.org/Number")))
						.requiredIntegerParameter("outputEvery", "Output Frequency", "Output values every " +
										"(seconds")
						.requiredIntegerParameter("timeWindow", "Time Window Size", "Size of the time window " +
										"in seconds")
						.requiredSingleValueSelection("operation", "Operation", "Aggregation operation type",
										Options.from("Average", "Sum", "Min", "Max"))
						.supportedFormats(StandardTransportFormat.standardFormat())
						.supportedProtocols(StandardTransportFormat.standardProtocols())
						.build();
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {

		ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(sepa);

		List<String> groupBy = SepaUtils.getMultipleMappingPropertyNames(sepa, "groupBy", true);

		String aggregate = SepaUtils.getMappingPropertyName(sepa, "aggregate");

		Integer outputEvery = extractor.singleValueParameter("outputEvery", Integer.class);
		Integer timeWindowSize = extractor.singleValueParameter("timeWindow", Integer.class);
		String aggregateOperation = extractor.selectedSingleValue("operation", String.class);

		List<String> selectProperties = new ArrayList<>();
		for (EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties()) {
			selectProperties.add(p.getRuntimeName());
		}

		AggregationParameter staticParam = new AggregationParameter(sepa, convert(aggregateOperation),
            outputEvery, groupBy,
				aggregate, timeWindowSize, selectProperties);

		return submit(staticParam, Aggregation::new, sepa);

	}

	private AggregationType convert(String aggregateOperation) {
		if (aggregateOperation.equals("Average"))
			return AggregationType.AVG;
		else if (aggregateOperation.equals("Sum"))
			return AggregationType.SUM;
		else if (aggregateOperation.equals("Min"))
			return AggregationType.MIN;
		else
			return AggregationType.MAX;
	}
}