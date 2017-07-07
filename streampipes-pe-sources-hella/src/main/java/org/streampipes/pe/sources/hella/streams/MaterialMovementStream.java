package org.streampipes.pe.sources.hella.streams;

import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.pe.sources.hella.config.HellaVariables;
import org.streampipes.pe.sources.hella.main.AbstractHellaStream;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
