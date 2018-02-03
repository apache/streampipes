package org.streampipes.manager.matching.output;

import java.util.List;

import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.output.FixedOutputStrategy;

public class FixedOutputSchemaGenerator implements OutputSchemaGenerator<FixedOutputStrategy> {

	private List<EventProperty> fixedProperties;
	
	public FixedOutputSchemaGenerator(List<EventProperty> fixedProperties) {
		this.fixedProperties = fixedProperties;
	}
	
	@Override
	public EventSchema buildFromOneStream(SpDataStream stream) {
		return new EventSchema(fixedProperties);
	}

	@Override
	public EventSchema buildFromTwoStreams(SpDataStream stream1,
			SpDataStream stream2) {
		return buildFromOneStream(stream1);
	}

	@Override
	public FixedOutputStrategy getModifiedOutputStrategy(
			FixedOutputStrategy strategy) {
		return strategy;
	}

}
