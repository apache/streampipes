package de.fzi.cep.sepa.manager.matching.output;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;

public class RenameOutputSchemaGenerator implements OutputSchemaGenerator<RenameOutputStrategy> {

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
		properties.addAll(new PropertyDuplicateRemover(properties, stream2.getEventSchema().getEventProperties()).rename());
		
		resultSchema.setEventProperties(properties);
		return resultSchema;
	}

	@Override
	public RenameOutputStrategy getModifiedOutputStrategy(
			RenameOutputStrategy strategy) {
		return strategy;
	}

}
