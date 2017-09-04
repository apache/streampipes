package org.streampipes.pe.processors.esper.filter.numerical;

import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.pe.processors.esper.util.NumericalOperator;
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

public class NumericalFilterController extends StandaloneEventProcessorDeclarerSingleton<NumericalFilterParameter> {

	@Override
	public SepaDescription declareModel() {
		return ProcessingElementBuilder.create("numericalfilter", "Numerical Filter", "Numerical Filter Description")
						.category(EpaType.FILTER)
						.iconUrl(EsperConfig.getIconUrl("Numerical_Filter_Icon_HQ"))
						.requiredPropertyStream1WithUnaryMapping(EpRequirements.numberReq(), "number", "Specifies the field name where the filter operation should be applied on.", "")
						.outputStrategy(OutputStrategies.keep())
						.requiredSingleValueSelection("operation", "Filter Operation", "Specifies the filter " +
										"operation that should be applied on the field", Options.from("<", "<=", ">", ">=", "=="))
						.requiredFloatParameter("value", "Threshold value", "Specifies a threshold value.")
						.supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
						.supportedFormats(SupportedFormats.jsonFormat())
						.build();

	}

	@Override
	public ConfiguredEventProcessor<NumericalFilterParameter, EventProcessor<NumericalFilterParameter>> onInvocation
					(SepaInvocation sepa) {
		ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(sepa);

		Double threshold = extractor.singleValueParameter("value", Double.class);
		String stringOperation = extractor.selectedSingleValue("operation", String.class);

		String operation = "GT";

		if (stringOperation.equals("<=")) operation = "LT";
		else if (stringOperation.equals("<")) operation="LE";
		else if (stringOperation.equals(">=")) operation = "GE";
		else if (stringOperation.equals("==")) operation = "EQ";

		String filterProperty = SepaUtils.getMappingPropertyName(sepa,
						"number", true);

		NumericalFilterParameter staticParam = new NumericalFilterParameter(sepa, threshold, NumericalOperator.valueOf(operation)
						, filterProperty);

		return new ConfiguredEventProcessor<>(staticParam, NumericalFilter::new);
	}
}
