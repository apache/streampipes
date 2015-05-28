package de.fzi.cep.sepa.manager.matching.output;

import java.util.List;

import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;

public class FixedOutputSchemaGenerator implements OutputSchemaGenerator {

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

}
