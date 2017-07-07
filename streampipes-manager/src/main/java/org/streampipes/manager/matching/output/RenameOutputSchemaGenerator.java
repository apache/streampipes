package org.streampipes.manager.matching.output;

import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.output.RenameOutputStrategy;

import java.util.ArrayList;
import java.util.List;

public class RenameOutputSchemaGenerator implements OutputSchemaGenerator<RenameOutputStrategy> {

	private RenameOutputStrategy strategy;

	public RenameOutputSchemaGenerator(RenameOutputStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public EventSchema buildFromOneStream(EventStream stream) {
		return stream.getEventSchema();
	}

	@Override
	public EventSchema buildFromTwoStreams(EventStream stream1,
			EventStream stream2) {
		EventSchema resultSchema = new EventSchema();
		List<EventProperty> properties = new ArrayList<>();
		properties.addAll(stream1.getEventSchema().getEventProperties());
		if (strategy.isKeepBoth()) {
			properties.addAll(new PropertyDuplicateRemover(properties,
							stream2.getEventSchema().getEventProperties()).rename());
		}
		
		resultSchema.setEventProperties(properties);
		return resultSchema;
	}

	@Override
	public RenameOutputStrategy getModifiedOutputStrategy(
			RenameOutputStrategy strategy) {
		return strategy;
	}

}
