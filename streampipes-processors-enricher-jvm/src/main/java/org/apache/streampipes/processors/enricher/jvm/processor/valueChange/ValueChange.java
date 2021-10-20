package org.apache.streampipes.processors.enricher.jvm.processor.valueChange;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

public class ValueChange implements EventProcessor<ValueChangeParameters> {

	@Override
	public void onInvocation(ValueChangeParameters valueChangeParameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext eventProcessorRuntimeContext) throws SpRuntimeException {

	}

	@Override
	public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {

	}

	@Override
	public void onDetach() throws SpRuntimeException {

	}
}
