package de.fzi.cep.sepa.manager.matching.output;

import java.util.List;

import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;

public class AppendOutputSchemaGenerator implements OutputSchemaGenerator {

	private List<EventProperty> appendProperties;
	
	public AppendOutputSchemaGenerator(List<EventProperty> appendProperties) {
		this.appendProperties = appendProperties;
	}
	
	@Override
	public EventSchema buildFromOneStream(EventStream stream) {
		appendProperties.addAll(new PropertyDuplicateRemover(appendProperties, stream.getEventSchema().getEventProperties()).rename());
		return new EventSchema(appendProperties);
	}

	@Override
	public EventSchema buildFromTwoStreams(EventStream stream1,
			EventStream stream2) {
		appendProperties.addAll(new PropertyDuplicateRemover(appendProperties, stream1.getEventSchema().getEventProperties()).rename());
		appendProperties.addAll(new PropertyDuplicateRemover(appendProperties, stream2.getEventSchema().getEventProperties()).rename());
		return new EventSchema(appendProperties);
	}

}
