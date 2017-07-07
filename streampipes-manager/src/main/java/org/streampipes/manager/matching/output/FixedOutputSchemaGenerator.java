package org.streampipes.manager.matching.output;

import java.util.List;

import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.output.FixedOutputStrategy;

public class FixedOutputSchemaGenerator implements OutputSchemaGenerator<FixedOutputStrategy> {

	private List<EventProperty> fixedProperties;
	
	public FixedOutputSchemaGenerator(List<EventProperty> fixedProperties) {
		this.fixedProperties = fixedProperties;
	}
	
	@Override
	public EventSchema buildFromOneStream(EventStream stream) {
		return new EventSchema(fixedProperties);
	}

	@Override
	public EventSchema buildFromTwoStreams(EventStream stream1,
			EventStream stream2) {
		return buildFromOneStream(stream1);
	}

	@Override
	public FixedOutputStrategy getModifiedOutputStrategy(
			FixedOutputStrategy strategy) {
		return strategy;
	}

}
