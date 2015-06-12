package de.fzi.cep.sepa.manager.matching.output;

import java.util.List;

import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;

public class CustomOutputSchemaGenerator implements OutputSchemaGenerator<CustomOutputStrategy> {

	private List<EventProperty> customProperties;
	
	public CustomOutputSchemaGenerator(List<EventProperty> customProperties) {
		this.customProperties = customProperties;
	}
	
	@Override
	public EventSchema buildFromOneStream(EventStream stream) {
		return new EventSchema(customProperties);
	}

	@Override
	public EventSchema buildFromTwoStreams(EventStream stream1,
			EventStream stream2) {
		return buildFromOneStream(stream1);
	}

	@Override
	public CustomOutputStrategy getModifiedOutputStrategy(
			CustomOutputStrategy strategy) {
		return strategy;
	}
}
