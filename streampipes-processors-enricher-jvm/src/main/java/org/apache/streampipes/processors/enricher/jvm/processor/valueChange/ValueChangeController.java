package org.apache.streampipes.processors.enricher.jvm.processor.valueChange;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.standalone.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

public class ValueChangeController extends StreamPipesDataProcessor {
	private static final String CHANGEVALUE_MAPPING = "changevalue-mapping";
	private static final String IS_CHANGED = "isChanged";

	private boolean isValueChanged = false;
	private double initValue;

	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder.create("org.apache.streampipes.enricher.jvm.valueChange","ValueChange","A value change data processor which return a boolean on data change")
				.category(DataProcessorType.TRANSFORM)
				.withAssets(Assets.DOCUMENTATION, Assets.ICON)
				.withLocales(Locales.EN)
				.requiredStream(StreamRequirementsBuilder
						.create()
						.requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
							Labels.withId(CHANGEVALUE_MAPPING),
							PropertyScope.NONE)
						.build())
				.outputStrategy(OutputStrategies.append(EpProperties.booleanEp(Labels.withId(IS_CHANGED),
					IS_CHANGED,SO.Boolean)))
				.build();
	}

	@Override
	public void onInvocation(ProcessorParams processorParams, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext eventProcessorRuntimeContext) throws SpRuntimeException {
		String initValue = processorParams.extractor().mappingPropertyValue(CHANGEVALUE_MAPPING);
		this.initValue = Double.parseDouble(initValue);
	}

	@Override
	public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
		Double currValue = event.getFieldBySelector(IS_CHANGED).getAsPrimitive().getAsDouble();
		if(currValue == this.initValue)
			event.addField(IS_CHANGED,false);
		event.addField(IS_CHANGED,true);
	}

	@Override
	public void onDetach() throws SpRuntimeException {

	}
}
