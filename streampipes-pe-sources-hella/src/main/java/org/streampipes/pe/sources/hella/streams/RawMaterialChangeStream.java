package org.streampipes.pe.sources.hella.streams;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.commons.Utils;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.pe.sources.hella.config.HellaVariables;
import org.streampipes.pe.sources.hella.main.AbstractHellaStream;

public class RawMaterialChangeStream extends AbstractHellaStream {

	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = prepareStream(HellaVariables.RawMaterialChange.topic());
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "", Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "machineId", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "materialId", "", Utils.createURI(SO.Text)));
		
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(HellaVariables.RawMaterialChange.eventName());
		stream.setDescription(HellaVariables.RawMaterialChange.description());
		stream.setUri(sep.getUri() + "/rawMaterialChange");
		
		return stream;
	}

}
