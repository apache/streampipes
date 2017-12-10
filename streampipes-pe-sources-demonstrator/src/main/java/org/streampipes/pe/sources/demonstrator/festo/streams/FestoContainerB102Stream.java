package org.streampipes.pe.sources.demonstrator.festo.streams;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.commons.Utils;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;
import org.streampipes.pe.sources.demonstrator.config.DemonstratorVariables;
import org.streampipes.pe.sources.demonstrator.config.DemonstratorConfig;
import org.streampipes.pe.sources.demonstrator.sources.AbstractDemonstratorStream;

public class FestoContainerB102Stream  extends AbstractDemonstratorStream {

	public FestoContainerB102Stream(DemonstratorVariables variables) {
		super(variables);
	}

	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		SpDataStream stream = prepareStream(variables.topic());

		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "timestamp", "",
				Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._float.toString(), "level", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._boolean.toString(), "underflow", "",
				Utils.createURI(SO.Boolean)));

		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(variables.eventName());
		stream.setDescription(variables.description());
		stream.setUri(sep.getUri() +"/" +variables.tagNumber());
		stream.setIconUrl(DemonstratorConfig.iconBaseUrl + "/" +variables.icon() +".png");
		return stream;
	}

}