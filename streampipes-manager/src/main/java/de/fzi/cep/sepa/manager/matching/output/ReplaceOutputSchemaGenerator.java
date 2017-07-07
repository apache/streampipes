package de.fzi.cep.sepa.manager.matching.output;

import java.net.URI;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.output.ReplaceOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.UriPropertyMapping;

public class ReplaceOutputSchemaGenerator implements OutputSchemaGenerator<ReplaceOutputStrategy>{

	private ReplaceOutputStrategy strategy;
	
	public ReplaceOutputSchemaGenerator(ReplaceOutputStrategy strategy) {
		this.strategy = strategy;
	}
	
	@Override
	public EventSchema buildFromOneStream(EventStream stream) {
		List<EventProperty> properties = stream.getEventSchema().getEventProperties();
		
		for(UriPropertyMapping replaceProperty : strategy.getReplaceProperties()) {
		System.out.println(replaceProperty.getReplaceTo().toASCIIString());
			EventProperty property = findPropertyById(replaceProperty.getReplaceTo(), properties);
			EventProperty newProperty = clone(property);
			if (replaceProperty.isRenamingAllowed()) newProperty.setRuntimeName(replaceProperty.getReplaceWith().getRuntimeName());
			
			properties.remove(property);
			properties.add(newProperty);
		}
		return new EventSchema(properties);
	}

	private EventProperty clone(EventProperty property) {
		if (property instanceof EventPropertyPrimitive) return new EventPropertyPrimitive((EventPropertyPrimitive) property);
		return null;
	}

	private EventProperty findPropertyById(URI replaceTo,
			List<EventProperty> properties) {
		return properties
				.stream()
				.filter(p -> p.getElementId().equals(replaceTo.toString()))
				.findFirst()
				.get();
	}

	@Override
	public EventSchema buildFromTwoStreams(EventStream stream1,
			EventStream stream2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStrategy getModifiedOutputStrategy(
			ReplaceOutputStrategy outputStrategy) {
		return outputStrategy;
	}

}
