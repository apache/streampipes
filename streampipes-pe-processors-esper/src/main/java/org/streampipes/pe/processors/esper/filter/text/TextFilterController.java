package org.streampipes.pe.processors.esper.filter.text;

import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.pe.processors.esper.util.StringOperator;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.ConfiguredEventProcessor;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;



public class TextFilterController extends StandaloneEventProcessorDeclarerSingleton<TextFilterParameter> {
	
	@Override
	public SepaDescription declareModel() {
		return ProcessingElementBuilder.create("textfilter", "Text Filter", "Text Filter Description")
						.iconUrl(EsperConfig.getIconUrl("Textual_Filter_Icon_HQ"))
						.category(EpaType.FILTER)
						.requiredPropertyStream1WithUnaryMapping(EpRequirements.stringReq(),"text", "Select Text Property", "")
						.requiredSingleValueSelection("operation", "Select Operation", "", Options.from("MATCHES", "CONTAINS"))
						.requiredTextParameter("keyword", "Select Keyword", "")
						.outputStrategy(OutputStrategies.keep())
						.supportedFormats(SupportedFormats.jsonFormat())
						.supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
						.build();
	}

	@Override
	public ConfiguredEventProcessor<TextFilterParameter, EventProcessor<TextFilterParameter>> onInvocation
					(SepaInvocation sepa) {
		ProcessingElementParameterExtractor extractor = getExtractor(sepa);

		String keyword = extractor.singleValueParameter("keyword", String.class);
		String operation =extractor.selectedSingleValue("operation", String.class);
		String filterProperty = extractor.mappingPropertyValue("text");

		logger.info("Text Property: " +filterProperty);

		TextFilterParameter staticParam = new TextFilterParameter(sepa,
						keyword,
						StringOperator.valueOf(operation),
						filterProperty);

		return new ConfiguredEventProcessor<>(staticParam, TextFilter::new);
	}
}
