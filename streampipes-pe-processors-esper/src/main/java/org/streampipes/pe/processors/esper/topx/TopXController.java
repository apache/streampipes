package org.streampipes.pe.processors.esper.topx;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.List;

public class TopXController extends StandaloneEventProcessorDeclarerSingleton<TopXParameter> {

	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder.create("topX", "Top-X", "Aggregates an event stream and outputs a list of events order by a given property")
						.category(DataProcessorType.TRANSFORM)
						.requiredPropertyStream1WithUnaryMapping(EpRequirements.numberReq(), "sortBy", "Sort by: ", "")
						.naryMappingPropertyWithoutRequirement("unique", "Unique properties: ", "")
						.outputStrategy(OutputStrategies.list("list"))
						.requiredTextParameter("topx", "Number of events: ", "")
						.requiredSingleValueSelection("direction", "Direction: ", "",
										Options.from("Ascending", "Descending"))
						.supportedFormats(StandardTransportFormat.standardFormat())
						.supportedProtocols(StandardTransportFormat.standardProtocols())
						.build();
	}

	@Override
	public ConfiguredEventProcessor<TopXParameter> onInvocation(DataProcessorInvocation sepa, ProcessingElementParameterExtractor extractor) {

		String sortBy = extractor.mappingPropertyValue("sortBy");
		Integer limit = extractor.singleValueParameter("topx", Integer.class);
		String direction = extractor.selectedSingleValue("direction", String.class);

		List<String> uniqueProperties = extractor.mappingPropertyValues("unique");

		OrderDirection orderDirection;

		if (direction.equals("Ascending")) {
			orderDirection = OrderDirection.ASCENDING;
		}
		else {
			orderDirection = OrderDirection.DESCENDING;
		}

		TopXParameter staticParam = new TopXParameter(sepa, orderDirection, sortBy, "list", limit, uniqueProperties);

		return new ConfiguredEventProcessor<>(staticParam, TopX::new);
	}
}
