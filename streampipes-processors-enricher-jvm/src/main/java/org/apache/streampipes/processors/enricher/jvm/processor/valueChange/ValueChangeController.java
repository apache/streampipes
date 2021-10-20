package org.apache.streampipes.processors.enricher.jvm.processor.valueChange;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class ValueChangeController extends StandaloneEventProcessingDeclarer<ValueChangeParameters>{
	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder.create("org.apache.streampipes.processors.enricher.jvm.valueChange")
				.category(DataProcessorType.ENRICH)
				.withAssets(Assets.DOCUMENTATION, Assets.ICON)
				.withLocales(Locales.EN)
				.requiredStream(StreamRequirementsBuilder
						.create()
						.requiredProperty(EpRequirements.anyProperty())
						.build())
				.requiredStream(StreamRequirementsBuilder.create()
						.requiredProperty(EpRequirements.anyProperty()).build())
				.build();
	}

	@Override
	public ConfiguredEventProcessor<ValueChangeParameters>
	onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

		return new ConfiguredEventProcessor<>();
	}
}
