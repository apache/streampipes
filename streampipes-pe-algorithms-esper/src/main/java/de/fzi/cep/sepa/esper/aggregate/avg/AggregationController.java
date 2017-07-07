package de.fzi.cep.sepa.esper.aggregate.avg;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.extractor.ProcessingElementParameterExtractor;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.Options;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;

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