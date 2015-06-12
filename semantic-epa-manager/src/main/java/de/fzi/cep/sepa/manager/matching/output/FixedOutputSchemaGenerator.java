package de.fzi.cep.sepa.manager.matching.output;

import java.util.List;

import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;

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
