package org.streampipes.pe.processors.esper.topx;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.wrapper.ConfiguredEventProcessor;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.List;

public class TopXController extends StandaloneEventProcessorDeclarerSingleton<TopXParameter> {

	@Override
	public SepaDescription declareModel() {
		return ProcessingElementBuilder.create("topX", "Top-X", "Aggregates an event stream and outputs a list of events order by a given property")
						.category(EpaType.TRANSFORM)
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
	public ConfiguredEventProcessor<TopXParameter, EventProcessor<TopXParameter>> onInvocation(SepaInvocation sepa) {
		ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(sepa);

		String sortBy = extractor.mappingPropertyValue("sortBy");
		Integer limit = extractor.singleValueParameter("topx", Integer.class);
		String direction = extractor.selectedSingleValue("direction", String.class);

		List<String> uniqueProperties = extractor.mappingPropertyValues("unique", true);

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
