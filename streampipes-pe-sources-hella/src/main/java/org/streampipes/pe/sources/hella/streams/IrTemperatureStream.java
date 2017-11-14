package org.streampipes.pe.sources.hella.streams;

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
import org.streampipes.pe.sources.hella.config.HellaVariables;

public class IrTemperatureStream extends EnvironmentalDataStream {

	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		SpDataStream stream = prepareStream(HellaVariables.IrTemperature.topic());
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.addAll(getPreparedProperties());
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "irTemperature", "", Utils.createURI(SO.Number)));
				
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(HellaVariables.IrTemperature.eventName());
		stream.setDescription(HellaVariables.IrTemperature.description());
		stream.setUri(sep.getUri() + "/irtemperature");
		
		return stream;
	}

}
