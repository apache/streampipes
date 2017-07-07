package de.fzi.cep.sepa.manager.matching.output;

import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;

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
