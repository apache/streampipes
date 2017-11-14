package org.streampipes.pe.sources.hella.streams;

import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.SO;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.pe.sources.hella.config.HellaVariables;
import org.streampipes.pe.sources.hella.main.AbstractHellaStream;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaterialMovementStream extends AbstractHellaStream {

	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		
		SpDataStream stream = prepareStream(HellaVariables.MontracMovement.topic());
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(EpProperties.stringEp(Labels.empty(), "variable_type", SO.Text));
		eventProperties.add(EpProperties.longEp(Labels.empty(), "variable_timestamp", "http://schema.org/DateTime"));
		eventProperties.add(EpProperties.stringEp(Labels.empty(), "location", Arrays.asList(URI.create("http://hella.de/hella#montracLocationId"), URI.create(SO.Text))));
		eventProperties.add(EpProperties.stringEp(Labels.empty(), "event", Arrays.asList(URI.create("http://hella.de/hella#montracEvent"), URI.create(SO.Text))));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "shuttle", Arrays.asList(URI.create("http://hella.de/hella#shuttleId"), URI.create(SO.Number))));
		eventProperties.add(EpProperties.booleanEp(Labels.empty(), "rightPiece", "http://schema.org/Boolean"));
		eventProperties.add(EpProperties.stringEp(Labels.empty(), "leftPiece", "http://schema.org/Boolean"));
			
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(HellaVariables.MontracMovement.eventName());
		stream.setDescription(HellaVariables.MontracMovement.description());
		stream.setUri(sep.getUri() + "/montrac");
		
		return stream;
	}

}
