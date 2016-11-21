package de.fzi.proasense.hella.streams;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.sdk.stream.EpProperties;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.proasense.hella.config.HellaVariables;
import de.fzi.proasense.hella.main.AbstractHellaStream;

public class MaterialMovementStream extends AbstractHellaStream {

	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = prepareStream(HellaVariables.MontracMovement.topic());
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(EpProperties.stringEp("variable_type", SO.Text));
		eventProperties.add(EpProperties.longEp("variable_timestamp", "http://schema.org/DateTime"));
		eventProperties.add(EpProperties.stringEp("location", Arrays.asList(URI.create("http://hella.de/hella#montracLocationId"), URI.create(SO.Text))));
		eventProperties.add(EpProperties.stringEp("event", Arrays.asList(URI.create("http://hella.de/hella#montracEvent"), URI.create(SO.Text))));
		eventProperties.add(EpProperties.integerEp("shuttle", Arrays.asList(URI.create("http://hella.de/hella#shuttleId"), URI.create(SO.Number))));
		eventProperties.add(EpProperties.booleanEp("rightPiece", "http://schema.org/Boolean"));
		eventProperties.add(EpProperties.stringEp("leftPiece", "http://schema.org/Boolean"));
			
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(HellaVariables.MontracMovement.eventName());
		stream.setDescription(HellaVariables.MontracMovement.description());
		stream.setUri(sep.getUri() + "/montrac");
		
		return stream;
	}

}
